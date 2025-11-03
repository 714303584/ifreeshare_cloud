package com.ifreeshare.chat.netty.client;


import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.netty.server.ChatServer;
import com.ifreeshare.chat.netty.server.Clientor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ClientMessageHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        logger.info("channelRegistered! name:"+ctx.name()+" channel:"+ctx.channel().id().asShortText());
        Message message = (Message) msg;
        logger.info("channelRead:"+ JSON.toJSONString(message));
//        Message returnMessage = new Message();
//        returnMessage.setFrom("server:");
//        returnMessage.setMsgId(UUID.randomUUID().toString());
//        returnMessage.setBody("server");

//        ctx.write(returnMessage);
//        ctx.flush();

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        Clientor clientor = new Clientor();
        clientor.setClient(ctx);
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
        ctx.fireExceptionCaught(cause);
    }

}
