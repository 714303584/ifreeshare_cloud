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


    //注册的服务器ID
    //用来区分是否本机 用于消息转发功能
    private  String serverId;

    //是否为本地连接
    //不是本地连接需要转发到其他服务器
    private  boolean isLocalConnect = true;

    private int status; // 0: 初始化,  1： 正常,  -1： 死亡

    private  long lastMsgTime; //最后消息时间

    //客户端连接当前服务器才不为空
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

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public boolean isLocalConnect() {
        return isLocalConnect;
    }

    public void setLocalConnect(boolean localConnect) {
        isLocalConnect = localConnect;
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
        //本服务器连接 -- 直接进行消息发送
        if(this.isLocalConnect){
            this.client.write(msg);
            this.client.flush();
        }else{
            //非本服务器连接 -- 将消息转发
            //目前用redis进行转发
            //todo 这里需要抽象一下 设计一个接口，做几个实现方式，以便支持不同的转发方式
            //RPC，消息中间件
            ChatServer.forwardMsg(this.serverId, msg);
        }
        return true;
    }
}
