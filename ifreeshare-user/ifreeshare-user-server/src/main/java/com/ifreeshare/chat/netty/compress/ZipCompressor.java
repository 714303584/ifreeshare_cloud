package com.ifreeshare.chat.netty.compress;

import com.ifreeshare.chat.Compression;

import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * 将数据进行zip压缩
 */
public class ZipCompressor implements Compression {

    /**
     * 这里进行数据压缩 采用的zip压缩的方式
     * --
     * @param bytes
     * @return
     */
    @Override
    public byte[] compress(byte[] bytes) {
        Deflater deflater = new Deflater();
        deflater.setInput(bytes);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // 压缩数据
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();


    }
}
