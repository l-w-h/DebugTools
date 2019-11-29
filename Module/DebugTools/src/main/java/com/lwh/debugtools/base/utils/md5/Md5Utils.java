package com.lwh.debugtools.base.utils.md5;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lwh
 * @Date 2019/10/19 15:33
 * @description Md5Utils
 */
public class Md5Utils {
    /**
     * 加密
     * @param plaintext 明文
     * @return ciphertext 密文
     */
    public final static String  encrypt(String plaintext) {
        if (TextUtils.isEmpty(plaintext)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(plaintext.getBytes());
            String ciphertext = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                ciphertext += temp;
            }
            return ciphertext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
