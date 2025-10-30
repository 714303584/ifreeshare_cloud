package com.ifreeshare.web;

/** 统一返回结果 */
public class ResultData<T> {

  private String code = ResponseCode.OK.getCode().toString();

  private String msg = ResponseCode.OK.getMsg();

  private T data;

  public ResultData(String code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public ResultData() {}

  public static ResultData defalut() {
    return new ResultData();
  }

  public static ResultData fail(Integer code, String msg) {
    return new ResultData(code.toString(), msg, null);
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Object getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
