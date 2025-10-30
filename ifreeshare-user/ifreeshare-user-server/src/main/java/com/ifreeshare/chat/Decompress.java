package com.ifreeshare.chat;

import com.ifreeshare.chat.netty.compress.ZipDecompressor;

public interface Decompress {


    byte[] decompress(byte[] bytes) ;

    /**
     * 获取一个压缩对象
     * @param type
     * @return
     */
    public static Decompress  getDecompressor(String type){
        //todo 待编码 -- 进行多压缩模式的扩展
        return new ZipDecompressor();
    }
}
