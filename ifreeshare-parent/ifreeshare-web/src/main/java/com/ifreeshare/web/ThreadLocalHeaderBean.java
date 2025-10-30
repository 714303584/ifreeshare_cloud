package com.ifreeshare.web;

/**
 *  本地方法
 * @Author zhuss
 */
public class ThreadLocalHeaderBean {
    private static ThreadLocal<HeaderBean>  threadLocal = new ThreadLocal<>();
    /**
     * 请求头信息获取
     * @return
     */
    public static HeaderBean get(){
        return threadLocal.get();
    }

    /**
     * 获取请求信息设置
     * @return
     */
    public static void set(HeaderBean headerBean){
         threadLocal.set(headerBean);
    }


    /**
     * 获取请求信息
     * @return
     */
    public static void clean(){
        threadLocal.remove();
    }


}
