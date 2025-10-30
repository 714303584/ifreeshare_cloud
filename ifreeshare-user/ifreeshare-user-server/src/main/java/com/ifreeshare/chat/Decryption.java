package com.ifreeshare.chat;

import java.security.PrivateKey;

/**
 * 对消息进行加密
 */
public interface Decryption {

     byte[] decrypt(byte[] cipherText);
}
