package com.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @create 2022/7/19 2:15 PM
 */
public class TestClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8002));
        while (true){
            socketChannel.write(Charset.defaultCharset().encode("hello world"));
            try {
                Thread.sleep(1000L);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}