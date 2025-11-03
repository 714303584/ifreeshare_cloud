package com.ifreeshare.chat.client;

public class MessageEnum {

    public enum Type{
        TEXT("1","文本"),
        IMG("2","图片"),
        VOICE("3","语音"),
        VIDEO("4","视频"),
        LOGIN("5","初始化"),
        PING("99999", "ping"), // 用于保持连接 -- 连接后需要先发ping事件
        PONG("100000", "pong"), // 用于保持连接 -- 连接后需要先发ping事件
        ACK("1000001", "ping"), // 用于告知客户端 -- 此消息已被服务器接收
        ;

        private String code;
        private String name;

        Type(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum FromType{
        CONNECT("0","连接"),
        PERSON_TO_PERSON("1","私聊"),
        GROUP("2","群组")
        ;

        private String code;
        private String name;

        FromType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
