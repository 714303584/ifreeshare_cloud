package com.ifreeshare.chat.netty.compress;

import com.ifreeshare.chat.Decompress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * 进行数据解压缩
 */
public class ZipDecompressor implements Decompress {
  /**
   * 这里进行数据压缩 采用的zip解压缩方式
   * @param bytes
   * @return
   */
  @Override
  public byte[] decompress(byte[] bytes) {
      // 假设这是通过Deflater压缩后的字节数组
      byte[] compressedData = {/* 这里是压缩数据 */};
      byte[] outputData = new byte[1024]; // 假设解压后的数据不会超过1024字节
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      Inflater inflater = new Inflater();
      inflater.setInput(bytes);
      inflater.finished();
      try {
          int resultLength;
          while (!inflater.finished()) { // 当Inflater完成解压时退出循环
              resultLength = inflater.inflate(outputData); // 解压数据到outputData数组中
              outputStream.write(outputData, 0, resultLength); // 将解压的数据写入到ByteArrayOutputStream中
          }
          outputStream.close(); // 关闭输出流
          inflater.end(); // 结束Inflater对象的使用，释放资源
      } catch (IOException e) {
          e.printStackTrace();
      } catch (DataFormatException e) {
          e.printStackTrace();
      }
      // 获取解压后的数据数组
      byte[] decompressedData = outputStream.toByteArray();
      return decompressedData;
  }


}
