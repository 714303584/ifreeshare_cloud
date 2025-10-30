package com.ifreeshare.chat.netty.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ifreeshare.chat.Decryption;
import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.Decompress;
import com.ifreeshare.tools.AesUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 进行消息解码
 */
public class MessageDeCodec extends MessageToMessageDecoder<ByteBuf> {
  Logger logger = LoggerFactory.getLogger(MessageDeCodec.class);
  private Decryption decryption;

//  public MessageDeCodec(){
//  }

  /**
   * 解密方式  -- 目前用于ras解密AES的密钥
   * @param decryption
   */
  public MessageDeCodec(Decryption decryption){
    this.decryption = decryption;
  }

  /**
   * 进行消息解码
   * @param channelHandlerContext
   * @param msg
   * @param list
   * @throws Exception
   */
  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list)
      throws Exception {
    //获取到的原始数据 -- 前64位为AES的密钥 -- 后面为正常的数据
    byte[] compressAfter = ByteBufUtil.getBytes(msg);

    //获取AES的密钥 -- 后面进行解密 -- 解密后进行AES解密
    byte[] AesEncrypKey = ArrayUtils.subarray(compressAfter, 0,64);

    //获取消息体
    byte[] encrypData = ArrayUtils.subarray(compressAfter, 64,compressAfter.length);


    //先解密后解压 --- 对应先压缩后加密
    //这里进行解密  -- 解密获取AES加密的key和标签 -- 这里key长度16，iv长度12
    byte[] keyAndIv = decryption.decrypt(AesEncrypKey);
    //获取AES密钥
    byte[] keys = ArrayUtils.subarray(keyAndIv,0,16);
    //获取加密的IV
    byte[] iv = ArrayUtils.subarray(keyAndIv,16,keyAndIv.length);

    //进行AES解密获取压缩后的消息体
    byte[] messageBody = AesUtils.decrypt(encrypData,keys,iv);

    // 1.获取消息体的字节 -- 进行消息体解压
    byte[] bytes = Decompress.getDecompressor("zip").decompress(messageBody);
    // 2.进行字节解压缩

    // 这里暂时解码为字符串
    // 后期拓展为加解密工具
    String message =
        new String(bytes, Charset.defaultCharset()); // msg.toString(Charset.defaultCharset());
    logger.info("message:" + message);
    JSONObject jsonObject = JSON.parseObject(message);
    Message message1 = JSON.toJavaObject(jsonObject, Message.class);
    list.add(message1);
  }
}
