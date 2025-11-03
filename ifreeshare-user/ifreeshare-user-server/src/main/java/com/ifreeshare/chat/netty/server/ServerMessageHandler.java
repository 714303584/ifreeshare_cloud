package com.ifreeshare.chat.netty.server;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.client.MessageEnum;
import com.ifreeshare.chat.netty.server.process.LoginMessageProcessor;
import com.ifreeshare.chat.netty.server.process.MessageProcessor;
import com.ifreeshare.chat.netty.server.process.PingMessageProcess;
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

    /**
     * 这里是消息处理器
     *
     */
    public static  Map<String, MessageProcessor> messageProcessors = new HashMap<>();
    static {
        //进行消息处理器的注册
        //登陆消息注册
        //实际登陆时走到是用户的登陆
        //提供用户名密码 用户获取Token
        //带token发送登陆消息
        //其实这里login消息发不发都可以的 -- 因为每一个消息都需要带Token进行来--不带token会被踢出去
        messageProcessors.put(MessageEnum.Type.LOGIN.getCode(),new LoginMessageProcessor());

        //这里注册一个保活消息的处理 -- 这个对TCP消息是无所谓的 -- 连接了就是活着
        messageProcessors.put(MessageEnum.Type.PING.getCode(),new PingMessageProcess());
        //这里处理文本消息 -- 其实所有消息都是文本消息 --
        //其他类型的消息（图片，音频， 视频）都是采用先上传到文件服务器，再将文件路径通过文本的方式发送给接收人。
        //客户端区分并进行显示
        messageProcessors.put(MessageEnum.Type.TEXT.getCode(),new TextMessageProcessor());
        //todo 图片，音频，视频 后期客户端工作的时候再添加
    }
    Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);

    /**
     * 进行消息处理
     * @param ctx
     * @param msg
     */
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
           //todo
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


    /**
     * 出现异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.info("channel Unregistered:"+ctx.channel().id().asShortText());
        ctx.disconnect();
        Clientor clientor = ChatServer.channelToClientor.remove(ctx.channel().id().asShortText());
        clientor.setStatus(Clientor.DEAD_STATUS);
        clientor.setLastMsgTime(-1);
        ChatServer.cleanClient();
        ctx.fireExceptionCaught(cause);
    }

    /**
     * 用户进行注销
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Clientor clientor = ChatServer.channelToClientor.remove(ctx.channel().id().asShortText());
        logger.info("channel Unregistered:"+ctx.channel().id().asShortText());
        clientor.setStatus(Clientor.DEAD_STATUS);
        clientor.setLastMsgTime(-1);
        ChatServer.cleanClient();
        ctx.fireChannelUnregistered();
    }

}
