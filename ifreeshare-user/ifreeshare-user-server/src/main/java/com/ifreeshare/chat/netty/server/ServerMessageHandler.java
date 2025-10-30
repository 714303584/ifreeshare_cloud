package com.ifreeshare.chat.netty.server;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.process.LoginMessageProcessor;
import com.ifreeshare.chat.netty.server.process.MessageProcessor;
import com.ifreeshare.chat.netty.server.process.TextMessageProcessor;
import com.ifreeshare.tools.JwtUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerMessageHandler extends ChannelInboundHandlerAdapter {

    //消息处理器
    public static  Map<String, MessageProcessor> messageProcessors = new HashMap<>();
    static {
        messageProcessors.put(MessageEnum.Type.LOGIN.getCode(),new LoginMessageProcessor());
        messageProcessors.put(MessageEnum.Type.TEXT.getCode(),new TextMessageProcessor());
    }
    Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        logger.info("channelRegistered! name:"+ctx.name()+" channel:"+ctx.channel().id().asShortText());
        Message message = (Message) msg;
        logger.info("message.body:"+message.getBody());
        String token = message.getFrom(); //用户token
        Clientor clientor = ChatServer.channelToClientor.get(ctx.channel().id().asShortText());
        //token不能为空
        if(!StringUtils.hasText(token)){
            //发送数据错误提示
            ctx.write(Message.authFailedMessage());
            ctx.flush();
            //关闭连接
            ctx.disconnect();

            ChatServer.channelToClientor.remove(ctx.channel().id().asShortText());
            clientor.setStatus(Clientor.DEAD_STATUS);
            ChatServer.cleanClient();
            return;
        }

        //消息类型
        String type =  message.getType();
        //获取消息处理体
        MessageProcessor messageProcessor = messageProcessors.get(type);
       //这里是连接状态
       if(messageProcessor != null){
            //处理消息
           messageProcessor.process(message, clientor);
       }else {
           Message returnMessage = new Message();
           returnMessage.setFrom("server:");
           returnMessage.setMsgId(UUID.randomUUID().toString());
           returnMessage.setBody("server");

           ctx.write(returnMessage);
           ctx.flush();
       }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        Clientor clientor = new Clientor();
        clientor.setClient(ctx);
        clientor.setLastMsgTime(System.currentTimeMillis());
        clientor.setStatus(Clientor.INIT_STATUS);
        //不包含则放入 -- 表示首次连接
        if(!ChatServer.channelToClientor.containsKey(ctx.channel().id().asShortText())){
            logger.info("首次进行连接! name:"+ctx.name()+" channel:"+ctx.channel());
            //放入连接
            ChatServer.channelToClientor.put(ctx.channel().id().asShortText(),clientor);
            ChatServer.linkedArrayList.addLast(clientor);
        }
        ctx.fireChannelRegistered();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.disconnect();
        Clientor clientor = ChatServer.channelToClientor.remove(ctx.channel().id().asShortText());
        clientor.setStatus(Clientor.DEAD_STATUS);
        clientor.setLastMsgTime(-1);
        ChatServer.cleanClient();
        ctx.fireExceptionCaught(cause);
    }

}
