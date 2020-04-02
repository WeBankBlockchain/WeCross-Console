package com.webank.wecross.console.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hash {
    public String getRandom(int numBytes) throws NoSuchAlgorithmException {
        SecureRandom.getInstance("SHA1PRNG");
        SecureRandom random = new SecureRandom();
        return bytesToHex(random.generateSeed(numBytes));
    }

    public String sha256(String msg) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest;
        String encodestr;
        messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(msg.getBytes("UTF-8"));
        encodestr = bytesToHex(messageDigest.digest());
        return encodestr;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
