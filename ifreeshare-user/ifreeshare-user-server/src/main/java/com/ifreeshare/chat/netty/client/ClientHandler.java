package com.ifreeshare.chat.netty.client;

import com.ifreeshare.chat.client.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Message message = new Message();
        message.setBody("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZnJlZXNoYXJlIiwidXNlcklkIjoiMSJ9.aXf80CJKcbbta3D46ipuVXhbWma__bCG5koDI1rUhAHl8nBhwJ7WqTCksmwLScC-8FtvWn0oS447pSbVSYvydw");

        ctx.write(message);
        System.out.println("Sent CONNECT");
    }
}
