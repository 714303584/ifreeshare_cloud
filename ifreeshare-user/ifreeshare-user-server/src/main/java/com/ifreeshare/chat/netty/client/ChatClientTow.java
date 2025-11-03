package com.ifreeshare.chat.netty.client;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.client.ChatClient;
import com.ifreeshare.chat.netty.client.ClientMessageHandler;
import com.ifreeshare.chat.netty.encryption.RasDecryption;
import com.ifreeshare.chat.netty.encryption.RasEncryption;
import com.ifreeshare.chat.netty.server.MessageDeCodec;
import com.ifreeshare.chat.netty.server.MessageEnCodec;
import com.ifreeshare.tools.PemUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Scanner;

@Component
public class ChatClientTow {
 // userid = 2
   public static final String token =
          "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZnJlZXNoYXJlIiwidXNlcklkIjoiMiJ9.3kWLidCsgzR3gnqhw_Ztp5xRLlnpRd1dfBsDYEghUgH72aNzRvYQXW9ggBg2Ahlous2a7WPexy33pmrs9AaRtA";


  public static String public_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/public.key";

  public static String private_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/private.key";

  public static RSAPublicKey rsaPublicKey = null;

  static {
    //获取公钥
    try {
      rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(public_key_file,"RSA");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  private static final String HOST = System.getProperty("host", "127.0.0.1");
  private static final int PORT = Integer.parseInt(System.getProperty("port", "1883"));

  public static void main(String[] args) {

      ChatClientTow chatClient = new ChatClientTow();
      chatClient.init();
    //
  }

  public void init() {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap();
      b.group(workerGroup);
      b.channel(NioSocketChannel.class);
      b.handler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(4096, 0, 4,0,4));
//              ch.pipeline().addLast(new StringDecoder());
                  ch.pipeline().addLast(new MessageDeCodec(new RasDecryption(rsaPublicKey)));
                  ch.pipeline().addLast(new MessageEnCodec(new RasEncryption(rsaPublicKey)));
                  ch.pipeline().addLast(new ClientMessageHandler());
                }
              });

      ChannelFuture f = b.connect(HOST, PORT).sync();
      Message messageEntity = new Message();
      // 这里首先发消息进行注册
      // todo 服务器端10秒未收到注册消息会立即断开

         messageEntity.setFrom(token);
      messageEntity.setType(MessageEnum.Type.LOGIN.getCode());
      f.channel().write(messageEntity);
      f.channel().flush();

      //消息ID
      int i = 1;
      //这里进行控制台输入
      Scanner scanner = new Scanner(System.in); // 创建Scanner对象
      //这里起了一个单线程 进行保活消息的发送
      new Thread(new Runnable() {
        @Override
        public void run() {
          int z = 1;
          while(true){
            z++;
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            Message message = new Message();
            message.setMsgId(z+"");
            message.setFrom(token);
            f.channel().writeAndFlush(message);
          }
        }
      }).start();
      for (;;){
        //读取一条信息
        System.out.println("请输入发送内容：");
        String messsageText = scanner.nextLine();

        //进行跳出
        if(messsageText.equals("exit()")){
          break;
        }
        //消息
        Message message2 = new Message();
        message2.setFrom(token);
        message2.setType(MessageEnum.Type.TEXT.getCode());
        message2.setMsgId(i+"");
        message2.setTo("1");
        message2.setBody(messsageText);
        f.channel().write(message2);
        f.channel().flush();
        i++;
      }
      scanner.close(); // 关闭S消息读取信息


      f.channel().closeFuture().sync();

    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
