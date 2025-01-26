package org.example.javafxdemo;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class EncryptionUtil {
    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int RSA_KEY_SIZE = 2048;
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_LENGTH = 12;
    public static final int TAG_LENGTH = 128;

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(RSA_KEY_SIZE);
        return keyGen.generateKeyPair();
    }

    private static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    public static String encrypt(String data, PublicKey rsaPublicKey) throws Exception {
        SecretKey aesKey = generateAESKey();

        byte[] ivBytes = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivBytes);

        Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, ivBytes);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
        byte[] encryptedData = aesCipher.doFinal(data.getBytes());

        Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());

        String encryptedAESKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);
        String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);

        return encryptedAESKeyBase64 + ":" + ivBase64 + ":" + encryptedDataBase64;
    }

    public static String decrypt(String encryptedData, PrivateKey rsaPrivateKey) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }

        byte[] encryptedAESKey = Base64.getDecoder().decode(parts[0]);
        byte[] ivBytes = Base64.getDecoder().decode(parts[1]);
        byte[] encryptedContent = Base64.getDecoder().decode(parts[2]);

        Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAESKey);

        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, ivBytes);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
        byte[] decryptedData = aesCipher.doFinal(encryptedContent);

        return new String(decryptedData);
    }
}
