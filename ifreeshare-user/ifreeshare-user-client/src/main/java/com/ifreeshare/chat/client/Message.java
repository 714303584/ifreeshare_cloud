package com.ifreeshare.chat.client;

/**
 * 消息载体
 */
public class Message {

    //消息ID
    private String msgId;
    //消息来源 -- 连接：0， 私聊：1, 群聊：2  3 ,
    private String fromType;
    //消息类型 1：文本消息， 2：图片， 3：语音消息
    private String type;
    //用户ID
    private String from;
    //用户ID
    private String to;
    //消息体
    private String body;

    //客户端ID -- 用于多客户端
    private  String clientId;

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public static Message authFailedMessage(){
        Message message = new Message();
        message.setFrom("system");
        message.setBody("授权失败 - 授权超时");
        return message;
    }

    public static Message textMessage(String body,String from,String to,String msgId){
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setMsgId(msgId);
        message.setType(MessageEnum.Type.TEXT.getCode());
        message.setBody(body);
        return message;
    }

}
