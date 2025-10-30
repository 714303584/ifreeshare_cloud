package com.ifreeshare.chat.netty.server;

import com.alibaba.fastjson.JSON;
import com.ifreeshare.chat.Encryption;
import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.Compression;
import com.ifreeshare.tools.AesUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.List;

/**
 * 进行数据编码
 *   这里进行数据编码
 *      数据格式 前四个字节为数据长度 后面是数据体（数据体采用压缩加密方式传输）
 */
public class MessageEnCodec extends MessageToMessageEncoder<Message> {
    Logger logger = LoggerFactory.getLogger(MessageEnCodec.class);
    private Encryption encryption;

    public MessageEnCodec(){

    }
    public MessageEnCodec(Encryption encryption){
        this.encryption = encryption;
    }

    //
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message o, List list) throws Exception {
        //将消息体解析为JSON格式字符串
        String message = JSON.toJSONString(o);
        byte[] compressBefor = message.getBytes();
        logger.info("compress befor length："+compressBefor.length);
        //这里进行数据压缩 --- 采用的默认压缩方式
        byte[] bytes = Compression.getCompressor("zip").compress(compressBefor);

        //获取加密Key
        SecretKey secretKey = AesUtils.generateAESKey();
        byte[] keyBytes = secretKey.getEncoded();
        //加密Key+iv 保存的byte
        byte[] aesKey  = {};
        //获取IV值
        byte[] iv = new byte[12]; // GCM推荐使用12字节的IV
        new SecureRandom().nextBytes(iv);
        //赋值
        aesKey = ArrayUtils.addAll(aesKey,keyBytes);
        aesKey = ArrayUtils.addAll(aesKey,iv);

        //进行值的加密 == 64位
        byte[] encryptbytes =  encryption.encrypt(aesKey);

        //采用
        //RASkey加密
        // 进行数据解密 --- RAS加密有数据长度的问题 所以不可以使用
//        bytes = encryption.encrypt(bytes);
        //这里进行
         bytes = AesUtils.encrypt(bytes,keyBytes,iv);

         //
        //todo -- 待处理 -- 进行数据的加密
        if(bytes.length > ChatServer.maxFrameLength){
            //数据包错误  -- 关闭此连接
            channelHandlerContext.pipeline().channel().close();
            throw new Exception("数据包过长--请缩短数据包");
        }
        //构建消息体
        //消息体 -- 前四个字节为消息长度 --  中间64为AES解密的Key的密文 -- 后面是消息内容
        ByteBuf payload = ByteBufAllocator.DEFAULT.buffer( 4 + 64 + bytes.length);


        //这里消息需要加密处理
        payload.writeInt(bytes.length+64).writeBytes(encryptbytes).writeBytes(bytes);
        logger.info("message length:"+bytes.length+"messageBody:"+message);
        list.add(payload);
    }


}
