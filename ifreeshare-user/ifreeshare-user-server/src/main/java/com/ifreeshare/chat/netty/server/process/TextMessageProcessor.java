package com.ifreeshare.chat.netty.server.process;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.ChatServer;
import com.ifreeshare.chat.netty.server.Clientor;
import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.tools.finals.TokenPayloadKeys;

import java.util.Map;
import java.util.Set;

public class TextMessageProcessor implements  MessageProcessor {
    @Override
    public void process(Message message, Clientor clientor) {

        Map<String, Object> userMap = JwtUtils.getTokenData(message.getFrom());
        String userId = userMap.get(TokenPayloadKeys.USER_ID).toString();

        //获取消息接收人
        String toId = message.getTo();
        Set<Clientor> clientorSet = ChatServer.getClients(toId);

        //
        Message returnMessage = new Message();
        returnMessage.setFrom("server");
        returnMessage.setMsgId(message.getMsgId());
        returnMessage.setType(MessageEnum.Type.TEXT.getCode());
        returnMessage.setBody("发送成功");

        for (Clientor c : clientorSet ) {
            c.writeAndFlush(Message.textMessage(message.getBody(),userId,toId,message.getMsgId()));
        }

        clientor.writeAndFlush(returnMessage);

    }
}
