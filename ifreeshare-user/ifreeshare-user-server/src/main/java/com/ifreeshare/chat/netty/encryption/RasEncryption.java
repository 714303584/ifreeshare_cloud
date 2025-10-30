package com.ifreeshare.chat.netty.encryption;

import com.ifreeshare.chat.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class RasEncryption implements Encryption {
    private Key key;

    public RasEncryption() {

    }

    public RasEncryption(Key key) {
        this.key = key;
    }

    @Override
    public byte[] encrypt(byte[] encryptBytes) {
        Cipher encryptCipher = null;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] resultByte = encryptCipher.doFinal(encryptBytes);
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
