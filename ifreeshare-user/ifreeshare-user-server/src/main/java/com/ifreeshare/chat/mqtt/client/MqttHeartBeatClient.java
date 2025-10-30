package com.ifreeshare.chat.mqtt.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public final class MqttHeartBeatClient {

    private MqttHeartBeatClient() {
    }

    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "1883"));
    private static final String CLIENT_ID = System.getProperty("clientId", "guestClient");
    private static final String USER_NAME = System.getProperty("userName", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZnJlZXNoYXJlIiwidXNlcklkIjoiMSJ9.aXf80CJKcbbta3D46ipuVXhbWma__bCG5koDI1rUhAHl8nBhwJ7WqTCksmwLScC-8FtvWn0oS447pSbVSYvydw");
    private static final String PASSWORD = System.getProperty("password", "guest");

    public static void main(String[] args) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("encoder", MqttEncoder.INSTANCE);
                    ch.pipeline().addLast("decoder", new MqttDecoder());
                    ch.pipeline().addLast("heartBeatHandler", new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("handler", new MqttHeartBeatClientHandler(CLIENT_ID, USER_NAME, PASSWORD));
                }
            });

            ChannelFuture f = b.connect(HOST, PORT).sync();
            String message = "ifreeshare";

            MqttFixedHeader connectFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false, 0);
            MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader("name",1);
            byte[] bytes = message.getBytes();
            ByteBuf payload = ByteBufAllocator.DEFAULT.buffer(bytes.length);
            payload.writeBytes(bytes);
            MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(connectFixedHeader, variableHeader,payload);
            f.channel().write(mqttPublishMessage);
            f.channel().flush();
            System.out.println("Client connected");
            f.channel().closeFuture().sync();

//            int index = 1;
//
//            while(true){
//                    Scanner scanner = new Scanner(System.in);
//
//                    if(scanner.hasNext()){
//
//
//                    }
//
//
//
//            }
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}