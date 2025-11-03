package com.ifreeshare.chat.netty.server.process;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.ChatServer;
import com.ifreeshare.chat.netty.server.Clientor;
import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.tools.finals.TokenPayloadKeys;

import java.util.Map;
import java.util.Set;

/**
 * 处理消息
 */
public class TextMessageProcessor implements  MessageProcessor {
    @Override
    public void process(Message message, Clientor clientor) {

        Map<String, Object> userMap = JwtUtils.getTokenData(message.getFrom());
        String userId = userMap.get(TokenPayloadKeys.USER_ID).toString();

        //获取发送人ID
        //这里有个问题 -- 多服务器时 -- 可能不在同一台服务器上
        //
        Set<Clientor> otherClient = ChatServer.getClients(userId);

        //这里判断下是否有其他客户端登陆 -- 有的话进行消息分发
        if(otherClient.size() > 1){
            //todo 进行消息分发
        }


        //获取消息接收人
        String toId = message.getTo();
        //这里获取一个列表 --
        //多平台发送
        //手机 电脑 浏览器做到同时接收（web平台需要自定义websocket进行接收 ）
        Set<Clientor> clientorSet = ChatServer.getClients(toId);

        //返回给发送者 -- 消息接收成功
        Message returnMessage = new Message();
        returnMessage.setFrom("server");
        returnMessage.setMsgId(message.getMsgId());
        returnMessage.setType(MessageEnum.Type.ACK.getCode());
        returnMessage.setBody("发送成功");

        for (Clientor c : clientorSet ) {
            c.writeAndFlush(Message.message(message.getBody(),userId,toId,message.getMsgId(),message.getType()));
        }

        clientor.writeAndFlush(returnMessage);

    }
}
