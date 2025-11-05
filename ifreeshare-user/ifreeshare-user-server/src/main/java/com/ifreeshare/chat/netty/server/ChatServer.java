package com.ifreeshare.chat.netty.server;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.algorithms.Algorithm;
import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.netty.encryption.RasDecryption;
import com.ifreeshare.chat.netty.encryption.RasEncryption;
import com.ifreeshare.tools.PemUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

  //redis -- 这里redis进行多服务器用户注册
  //进行跨服务器用户聊天时的消息交换
  public static RedissonClient redisson;

  //服务器的ID -- 用于标记用户注册到那个服务器
  // 同时也用来订阅其他服务器发送来的消息 --- 此次修改用来订阅其他服务器向本服务器发送的消息
  // 1. A收到一条未注册到本服务器的消息
  // 2. 通过redis查询到这条消息的接受者在C服务器
  // 3. 将消息直接丢到redis中C服务器的队列中
  // 4. C服务器获取队列中的消息
  // 6. C服务器将此消息发送给对应的接受者
  private static  String serverId = "chatSeverOne";

  //这里是当前服务器需要检测的队列名称
  //队列里的实体是其他服务器发送来的消息
  public  static  String messageQueueName = "message:"+serverId+":queue";
  //这里是客户端注册当前服务器名称的前缀
  //这里是一个set  用来存储这个用户连接了那几台服务器
  // User1 手机端 -- a
  //        pc端 -- c
  //        websocket -- e
  // user2 手机端 -- f
  // redis上对应的set就是 a,c,e 三台服务器的名字（serverId）
  // 若是进行消息转发的时候 -- a，c，e 三台服务器对应的队列名称为 message:a:queue，message:c:queue，message:e:queue
  // User2 向User1 发送消息时需要向redis对应的a，c，e三台服务器的队列里放入消息
  // a，c，e三台服务器从redis对应的队列里获取到消息信息 再转发给User1
  public  static  String clientRegistName = "client:regist:server:";


  /**
   *
   */
  public static final Algorithm algorithm ;

  public static String public_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/public.key";

  public static String private_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/private.key";

  public static  RSAPublicKey rsaPublicKey = null;

  //获取私钥
  public static   RSAPrivateKey rsaPrivateKey = null;

  static {

    //获取公钥
    try {
      rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(public_key_file,"RSA");
    } catch (IOException e) {
      e.printStackTrace();
    }


    try {
      rsaPrivateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(private_key_file,"RSA");
    } catch (IOException e) {
      e.printStackTrace();
    }
    algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);

  }


  private static Logger logger = LoggerFactory.getLogger(ChatServer.class);

  public static  int maxFrameLength =  4096;

  //用于进行超时
  public static LinkedList<Clientor> linkedArrayList = new LinkedList<Clientor>();

  //通道到连接
  //key：通道Id value：通道
  public  static Map<String,Clientor> channelToClientor = new ConcurrentHashMap();
  //用户到连接 == 用户id <> 客户端 -- 多客户端
  public  static Map<String, Set<Clientor>> userToClientor = new ConcurrentHashMap();



  public static void main(String[] args) {

    //进行redis消息连接初始化
    Config config = new Config();
    //设置redis的配置信息
    config.useSingleServer()
            .setAddress("redis://127.0.0.1:6379")
            .setDatabase(0); // 可选

    //初始化redis控制
     ChatServer.redisson = Redisson.create(config);

    System.out.println("default_charset:"+ Charset.defaultCharset());
    //
    ChatServer chatServer = new ChatServer();
    chatServer.init();
  }

  /**
   * 进行超时任务清理
   */
  public static void cleanClient() {

    if(linkedArrayList.size() == 0){
      return;
    }
    //进行数据清理
    while (true){
      Clientor clientor = linkedArrayList.getFirst();
      //todo 删除位置
      if(clientor.canClean()){
        linkedArrayList.removeFirst();
        logger.info("remove:"+clientor.getClient().channel().id());
      }else{
        break;
      }
    }

  }

  /**
   * 添加客户端
   */
  public static void putClient(String clientId, Clientor clientor){
    //所有客户端
    Set<Clientor> clientorSet = userToClientor.get(clientId);
    if(clientorSet == null){
      clientorSet = new HashSet<>(6);
      clientorSet.add(clientor);
      userToClientor.put(clientId,clientorSet);
    }else{
      clientorSet.add(clientor);
    }
    logger.info("register:"+clientId);
    //进行客户端添加
    RSet<String> redisClientServerSet = redisson.getSet(clientRegistName+clientId);
    redisClientServerSet.add(serverId);

  }


  /**
   * 获取本机客户端
   * @param clientId
   * @return
   */
  public static Set<Clientor> getLocalClients(String clientId) {
    // 所有本机客户端
      Set<Clientor> clientorSet = userToClientor.get(clientId);
      if (clientorSet == null) {
        clientorSet = new HashSet<>(6);
      }
      return clientorSet;
    }

  /**
   * 这里获取此用户的连接的客户端
   *  todo 这里需要加上多服务器版本
   *        redis记录一个用户在那台服务器上注册， 这里需要获取其他服务器上的连接的用户。
   *        当两个用户不在一台服务器上时，需要将当前用户发送的消息进行转发到另一台服务器上。
   *
   *       wo -> A
   *              |  redis --
   *       ni -> B
   * @param clientId
   * @return
   */
  public static Set<Clientor> getClients(String clientId){
    //所有本机客户端
    Set<Clientor> clientorSet = getLocalClients(clientId);
    //客户端注册地址
    RSet<String> registServerSet = redisson.getSet("client:regist:server:"+clientId);
    if(registServerSet.isExists()){
      //获取客户端所注册的服务器列表
      Iterator<String>  serverSetIterator =  registServerSet.iterator();
      //是否
      while(serverSetIterator.hasNext()){
        String serverId = serverSetIterator.next();
        //非本机注册客户端
        if(!ChatServer.serverId.equals(serverId)){
          //远程服务器注册
          Clientor clientor = new Clientor();
          clientor.setServerId(serverId);
          clientor.setLocalConnect(false);
          clientorSet.add(new Clientor());
        }
      }
    }
    return clientorSet;
  }


  /**
   * 进行消息转发
   * @param serverId 需要转发到的服务器Id
   * @param msg 需要转发的消息
   * @return
   */
  public static boolean forwardMsg(String serverId, Message msg){
    RBlockingQueue<String> messageBQueue = redisson.getBlockingQueue(messageQueueName);
    return messageBQueue.add(JSON.toJSONString(msg));
  }

  /**
   * 添加客户端
   */
  public static void removeClient(String clientId, Clientor clientor){
    //所有客户端
    Set<Clientor> clientorSet = userToClientor.get(clientId);
    if(clientorSet == null){
      clientorSet = new HashSet<>(6);
    }
    clientorSet.remove(clientor);
  }

  public void init() {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();


    /**
     * 开启一个线程 -- 进行转发数据获取
     *
     * //todo 这里需要改一下 --- 用线程池来处理
     */
    new Thread(new Runnable() {
      @Override
      public void run() {
        //这里是redis的队列
        //其他服务器将要发送的消息发送到这个队列中，本服务器再将消息取出发送给指定客户端
        RBlockingQueue<String> messageBlockingQueue = redisson.getBlockingQueue(ChatServer.messageQueueName) ;
        while(true){
          try {
            //同步阻塞
            String messageString = messageBlockingQueue.take();
            if(StringUtils.isEmpty(messageString)){
              continue;
            }
            logger.info("get forward messsage:"+messageString);
            Message message = JSON.parseObject(messageString,Message.class);
            Set<Clientor> clientorSet = ChatServer.getLocalClients(message.getTo());
            for (Clientor clientor : clientorSet) {
              logger.info("send to:"+message.getTo());
              clientor.writeAndFlush(message);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        }
      }
    }).start();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup);
      b.channel(NioServerSocketChannel.class);
      b.childHandler(
          new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              //使用包头长度读取
              //前四个字节为数据长度
              ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(maxFrameLength, 0, 4,0,4));
              //将读取到的数据包进行字符串解码  这里取消字符串解码
//              ch.pipeline().addLast(new StringDecoder());
              //将解析到的字符串解析为消息体
              ch.pipeline().addLast(new MessageDeCodec(new RasDecryption(rsaPrivateKey)));
              //进行字符串编码
              ch.pipeline().addLast(new MessageEnCodec(new RasEncryption(rsaPrivateKey)));
              //进行消息处理
              ch.pipeline().addLast(new ServerMessageHandler());
//              ch.pipeline().add
            }
          });

      ChannelFuture f = b.bind(1883).sync();

      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
