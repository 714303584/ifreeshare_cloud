package com.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public class BossThreadServer {




    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("bossGroup");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8002));
        //创建固定数量的worker去处理读写事件，并进行worker的初始化
        //一版建议设置为服务器的CPU核心数
        //bug：Runtime.getRuntime().availableProcessors()如果是部署在docker容器下，因为容器不是物理隔离的，所以该方法会拿到物理机的CPU个数，而不是容器申请的CPU个数
        //该问题在jdk10才修复，使用jvm参数UseContainerSupport配置，默认开启
        AtomicInteger sum = new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> keyIterator = boss.selectedKeys().iterator();
            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()){
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    //初始化worker并添加注册事件
                    System.out.println("注册成功");
                    SelectorThread thread = new SelectorThread();
                    socketChannel.register(thread.getMySelector(),
                            SelectionKey.OP_READ+SelectionKey.OP_CONNECT+SelectionKey.OP_WRITE);
                    thread.start();

                }
            }
        }
    }
}
