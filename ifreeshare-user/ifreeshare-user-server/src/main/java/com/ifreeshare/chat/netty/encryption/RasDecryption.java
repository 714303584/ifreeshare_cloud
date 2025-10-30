package com.ifreeshare.chat.netty.encryption;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ifreeshare.chat.Decryption;
import com.ifreeshare.tools.AesUtils;
import com.ifreeshare.tools.PemUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 *
 */
public class RasDecryption implements Decryption {

    Key key;
    public RasDecryption() {
    }


    /**
     *
     */
    public static final Algorithm algorithm ;

    public static String public_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/public.key";

    public static String private_key_file = "/Users/fangyuan/ifreeshare_cloud/ifreeshare-parent/ifreeshare-tools/src/main/java/com/ifreeshare/tools/private.key";

    public static  RSAPublicKey rsaPublicKey = null;

    //获取私钥
    public static   RSAPrivateKey rsaPrivateKey = null;

    static {
        //获取公钥
        try {
            rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(public_key_file,"RSA");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            rsaPrivateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(private_key_file,"RSA");
        } catch (IOException e) {
            e.printStackTrace();
        }
        algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);

    }


    public static void main(String[] args) {

        RasDecryption rd = new RasDecryption(rsaPublicKey);
        RasEncryption rasEncryption = new RasEncryption(rsaPrivateKey);
      try {
          SecretKey secretKey = AesUtils.generateAESKey();
          byte[] keyBytes = secretKey.getEncoded();

          System.out.println("keyBytes.length:"+keyBytes.length);

          String  data = "1111111111a;djfa;ojf;oweaj;afj;ajflsajfjaiej;iofjawiejfa;fj";



         byte[] aesKey  = {};
          byte[] iv = new byte[12]; // GCM推荐使用12字节的IV
          new SecureRandom().nextBytes(iv);
          aesKey = ArrayUtils.addAll(aesKey,keyBytes);
          aesKey = ArrayUtils.addAll(aesKey,iv);

          byte[] b = AesUtils.encrypt(data.getBytes(),keyBytes,iv);

          byte[] encryptbytes =  rasEncryption.encrypt(aesKey);

          System.out.println("encryptbytes.length:"+encryptbytes.length);


          byte[] debytes =  rd.decrypt(encryptbytes);

          aesKey = ArrayUtils.subarray(debytes,0,16);
          iv = ArrayUtils.subarray(debytes,16,28);


          System.out.println(new String(b, Charset.defaultCharset()));

          byte[] b2 = AesUtils.decrypt(b,aesKey,iv);

          System.out.println(new String(b2, Charset.defaultCharset()));


      } catch (Exception e) {
          e.printStackTrace();
      }
      //
  }

    public RasDecryption(Key key) {
        this.key = key;
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {

        Cipher encryptCipher = null;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] resultByte = encryptCipher.doFinal(cipherText);
            return resultByte;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
