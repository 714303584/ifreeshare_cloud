package com.ifreeshare.chat.netty.server;

import com.ifreeshare.chat.client.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端
 */
public class Clientor {

    public static final int INIT_STATUS = 0;

    public static final int nom_STATUS = 1;

    public static final int DEAD_STATUS = -1;


    private int status; // 0: 初始化,  1： 正常,  -1： 死亡

    private  long lastMsgTime; //最后消息时间

    ChannelHandlerContext client; //连接的客户端

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ChannelHandlerContext getClient() {
        return client;
    }

    public void setClient(ChannelHandlerContext client) {
        this.client = client;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public boolean isActive(){
         return status == nom_STATUS || (status == INIT_STATUS && System.currentTimeMillis() - lastMsgTime < 10000);
    }

    //是否可以在等待队列里释放
    // 逻辑暂定为 断开连接 正常有消息连接  和超出等待范围连接 可以从等待队列里释放
    public boolean canClean(){
        return (status == INIT_STATUS && System.currentTimeMillis() - lastMsgTime > 10000)
                || status == DEAD_STATUS
                || status == nom_STATUS;
    }

    public synchronized boolean  writeAndFlush(Message msg){
        this.client.write(msg);
        this.client.flush();
        return true;
    }
}
