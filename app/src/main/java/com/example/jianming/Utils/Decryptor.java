package com.example.jianming.Utils;


import com.example.jianming.myapplication.BuildConfig;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decryptor {

    public static byte[] decrypt(byte[] encrypted) {
        byte[] iv = "2017041621251234".getBytes();
        byte[] key = BuildConfig.password.getBytes();
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return cipher.doFinal(encrypted);
        } catch (NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 InvalidKeyException |
                 BadPaddingException |
                 IllegalBlockSizeException |
                 InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;

    }
}
