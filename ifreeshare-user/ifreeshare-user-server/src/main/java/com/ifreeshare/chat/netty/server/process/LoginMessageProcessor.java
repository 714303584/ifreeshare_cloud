package com.ifreeshare.chat.netty.server.process;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.ChatServer;
import com.ifreeshare.chat.netty.server.Clientor;
import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.tools.finals.TokenPayloadKeys;

import java.util.Map;
import java.util.UUID;

public class LoginMessageProcessor implements MessageProcessor {
    @Override
    public void process(Message message, Clientor clientor) {
        Map<String, Object> userMap = JwtUtils.getTokenData(message.getFrom());
        String userId = userMap.get(TokenPayloadKeys.USER_ID).toString();
        //添加client
        ChatServer.putClient(userId,clientor);

        Message returnMessage = new Message();
        returnMessage.setFrom("server");
        returnMessage.setMsgId(message.getMsgId());
        returnMessage.setType(MessageEnum.Type.LOGIN.getCode());
        returnMessage.setBody("登陆成功");
        clientor.getClient().write(returnMessage);
        clientor.getClient().flush();


    }
}