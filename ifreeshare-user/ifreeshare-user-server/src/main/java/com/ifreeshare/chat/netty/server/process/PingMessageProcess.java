package com.ifreeshare.chat.netty.server.process;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.Clientor;

/**
 * 客户端发送保活消息  ping 消息
 * 服务端发送收到消息 pong 消息
 *  像打乒乓球一样 -- 你打过来我抽回去
 *
 */
public class PingMessageProcess implements  MessageProcessor{
    /**
     * 进行屏消息的处理
     * 简单的回复一个pong类型的消息
     * @param message
     * @param clientor
     */
    @Override
    public void process(Message message, Clientor clientor) {
        Message pongMessage = new Message();
        pongMessage.setMsgId(message.getMsgId());
        pongMessage.setType(MessageEnum.Type.PONG.getCode());
        clientor.getClient().writeAndFlush(pongMessage);
    }
}
