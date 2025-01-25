package org.example.javafxdemo;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;
    private static final String SECRET_KEY = "1234567890123456";

    public static String encrypt(String data) throws Exception {
        byte[] ivBytes = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
        String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);

        return ivBase64 + ":" + encryptedBase64;
    }

    public static String decrypt(String encryptedData) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Wrong data format for decryption");
        }

        byte[] ivBytes = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }
}
