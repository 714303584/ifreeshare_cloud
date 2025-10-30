package com.ifreeshare.tools;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
public class RSADecryption {
    public static byte[] decrypt(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = decriptCipher.doFinal(Base64.getDecoder().decode(cipherText));
        return plainText;
    }
}
