package com.ifreeshare.chat.netty.server.process;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.ChatServer;
import com.ifreeshare.chat.netty.server.Clientor;
import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.tools.finals.TokenPayloadKeys;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * 登陆消息的处理
 *          客户端需要在连接成功后立即发送登陆消息
 *          若超时不发送登陆消息 --- 连接则会中断 默认时间为10秒
 */
public class LoginMessageProcessor implements MessageProcessor {
    /**
     * 处理登陆消息
     * @param message
     * @param clientor
     */
    @Override
    public void process(Message message, Clientor clientor) {
        //根据token获取消息发送者的ID
        Map<String, Object> userMap = JwtUtils.getTokenData(message.getFrom());
        String userId = userMap.get(TokenPayloadKeys.USER_ID).toString();
        Message returnMessage = new Message();
        returnMessage.setFrom("server");
        returnMessage.setMsgId(message.getMsgId());
        returnMessage.setType(MessageEnum.Type.LOGIN.getCode());
        if(StringUtils.isEmpty(userId)){
            //登陆失败
            returnMessage.setBody("登陆失败：用户登陆非法.");
            clientor.getClient().write(returnMessage);
            clientor.getClient().flush();
            clientor.getClient().disconnect();
        }else{
            //将客户端连接以用户id为key放入Map
            ChatServer.putClient(userId,clientor);
            // 返回登陆成功的消息
            returnMessage.setFrom("server");
            returnMessage.setMsgId(message.getMsgId());
            returnMessage.setType(MessageEnum.Type.LOGIN.getCode());
            returnMessage.setBody("登陆成功");
            clientor.getClient().write(returnMessage);
            clientor.getClient().flush();
        }

    }
}