package com.webank.wecross.console.common;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtility {
    private static Logger logger = LoggerFactory.getLogger(RSAUtility.class);

    /**
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        logger.info(
                "RSA pri: {}",
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        logger.info(
                "RSA Pub: {}",
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        return keyPair;
    }

    /**
     * @param
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptBase64(String encryptedData, PrivateKey privateKey)
            throws Exception {
        byte[] decodeBase64 = Base64.getDecoder().decode(encryptedData);
        return decrypt(decodeBase64, privateKey);
    }

    /**
     * @param
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    /**
     * @param sourceData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] sourceData, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(sourceData);
    }

    /**
     * @param sourceData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptBase64(byte[] sourceData, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(sourceData);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * @param content
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey createPublicKey(String content)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509KeySpec =
                new X509EncodedKeySpec(Base64.getDecoder().decode(content));
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        return pubKey;
    }

    /**
     * @param content
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey createPrivateKey(String content)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8KeySpec =
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        return priKey;
    }
}
