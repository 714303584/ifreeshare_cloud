package com.ifreeshare.chat.netty.server;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.mqtt.server.MqttHeartBeatBrokerHandler;
import com.ifreeshare.chat.netty.encryption.RasDecryption;
import com.ifreeshare.chat.netty.encryption.RasEncryption;
import com.ifreeshare.chat.netty.server.process.LoginMessageProcessor;
import com.ifreeshare.chat.netty.server.process.MessageProcessor;
import com.ifreeshare.chat.netty.server.process.TextMessageProcessor;
import com.ifreeshare.tools.PemUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.reactivex.rxjava3.internal.util.LinkedArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatServer {


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
      userToClientor.put(clientId,clientorSet);
    }
    logger.info("register:"+clientId);
    clientorSet.add(clientor);
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
    //所有客户端
    Set<Clientor> clientorSet = userToClientor.get(clientId);
    if(clientorSet == null){
      clientorSet = new HashSet<>(6);
    }
    return clientorSet;
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
