package com.ifreeshare.chat.netty.client;

import com.auth0.jwt.algorithms.Algorithm;
import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class ChatClient2 {



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

    ChatClient chatClient = new ChatClient();
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
      messageEntity.setFrom("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZnJlZXNoYXJlIiwidXNlcklkIjoiMiJ9.3kWLidCsgzR3gnqhw_Ztp5xRLlnpRd1dfBsDYEghUgH72aNzRvYQXW9ggBg2Ahlous2a7WPexy33pmrs9AaRtA");
      messageEntity.setType(MessageEnum.Type.LOGIN.getCode());
      f.channel().write(messageEntity);
      f.channel().flush();

      int i = 1;
      for (;;){
        Thread.sleep(1000);
        Message message2 = new Message();
        message2.setFrom("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZnJlZXNoYXJlIiwidXNlcklkIjoiMiJ9.3kWLidCsgzR3gnqhw_Ztp5xRLlnpRd1dfBsDYEghUgH72aNzRvYQXW9ggBg2Ahlous2a7WPexy33pmrs9AaRtA");
        message2.setType(MessageEnum.Type.TEXT.getCode());
        message2.setTo("1");
        message2.setBody("你好啊 客户端-- 我是测试小猪"+i);
        f.channel().write(message2);
        f.channel().flush();
        i++;
      }


//      f.channel().closeFuture().sync();

    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
