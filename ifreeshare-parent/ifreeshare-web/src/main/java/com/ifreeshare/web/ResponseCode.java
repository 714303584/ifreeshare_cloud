package com.ifreeshare.web;

public enum ResponseCode {

    OK(0,"ok"),
    USER_NOT_FOUND(40001, "用户未找到"),
    NO_LOGIN_ERROR(401,"请先完成登陆")
    ;


    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    ResponseCode(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
