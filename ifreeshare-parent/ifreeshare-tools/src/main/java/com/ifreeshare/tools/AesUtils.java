package com.ifreeshare.tools;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtils {

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 可以选择128, 192或256位密钥长度
        return keyGenerator.generateKey();
    }

    public static byte[] encrypt(byte[] data, byte[] keyByte,byte[] iv) throws Exception {
//        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyByte,"AES"),gcmParameterSpec);
        return cipher.doFinal(data);
    }


    public static byte[] decrypt(byte[] data, byte[] keyByte, byte[] iv) throws Exception {

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyByte,"AES"),gcmParameterSpec);
       return cipher.doFinal(data);
    }


}
