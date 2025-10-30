package com.ifreeshare.tools;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAEncryption {
    public static byte[] encrypt(byte[] bytes, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = encryptCipher.doFinal(bytes);
        return cipherText; // 返回Base64编码的字符串，便于传输和存储
    }
}
