package com.demo.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;

public class SelectorThread extends Thread {

    public static Integer index = 0;

//    private  String name;

    private HashSet<SocketChannel> socketChannelHashSet = new HashSet<>();


    public SelectorThread(){
        this.setName( "SelectorThread_"+(++index));
        try {
            this.mySelector =  SelectorProvider.provider().openSelector();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Selector mySelector;


    @Override
    public void run() {
        while(true){
            try {
                Integer selectKeys =  mySelector.select();
              Iterator<SelectionKey> iterable =  mySelector.selectedKeys().iterator();
              while (iterable.hasNext()){
                 SelectionKey selectionKey = iterable.next();
                  SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                 if(selectionKey.isReadable()){
                     if(!socketChannel.isOpen()){
                         break;
                     }
                     ByteBuffer byteBuffers = ByteBuffer.allocate(2048);
                     socketChannel.read(byteBuffers);
                     System.out.println(this.getName()+":"+new String(byteBuffers.array(), Charset.defaultCharset()));

                 }else if(selectionKey.isConnectable()){
                     break;
                 }

              }




            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Selector getMySelector() {
        return mySelector;
    }

    public void setMySelector(Selector mySelector) {
        this.mySelector = mySelector;
    }
}
