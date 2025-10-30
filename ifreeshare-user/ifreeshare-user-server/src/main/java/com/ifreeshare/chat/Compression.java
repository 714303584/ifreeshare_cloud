package com.ifreeshare.chat;

import com.ifreeshare.chat.netty.compress.ZipCompressor;

public interface Compression  {

    byte[] compress(byte[] bytes) ;

    /**
     * 获取一个压缩对象
     * @param type
     * @return
     */
    public static Compression  getCompressor(String type){
        //todo 待编码 -- 进行多压缩模式的扩展
        return new ZipCompressor();
    }
}
