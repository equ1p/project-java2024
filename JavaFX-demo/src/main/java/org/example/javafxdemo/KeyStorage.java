package org.example.javafxdemo;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class KeyStorage {
    public static void saveKeysToDatabase(PublicKey publicKey, PrivateKey privateKey) throws Exception {
        String sql = "INSERT INTO rsa_keys (public_key, private_key) VALUES (?, ?)";
        try (Connection conn = DataBase.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBytes(1, publicKey.getEncoded());
            pstmt.setBytes(2, privateKey.getEncoded());
            pstmt.executeUpdate();
        }
    }

    public static KeyPair loadKeysFromDatabase() throws Exception {
        String sql = "SELECT public_key, private_key FROM rsa_keys LIMIT 1";
        try (Connection conn = DataBase.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                byte[] publicKeyBytes = rs.getBytes("public_key");
                byte[] privateKeyBytes = rs.getBytes("private_key");

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

                return new KeyPair(publicKey, privateKey);
            }
        }
        return null;
    }

    public static boolean keysExist() throws Exception {
        String sql = "SELECT COUNT(*) AS count FROM rsa_keys";
        try (Connection conn = DataBase.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    public static KeyPair loadUserKeys(String username) throws Exception {
        String sql = "SELECT public_key, private_key FROM users WHERE username = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] publicKeyBytes = rs.getBytes("public_key");
                    byte[] privateKeyBytes = rs.getBytes("private_key");

                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                    PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

                    return new KeyPair(publicKey, privateKey);
                }
            }
        }
        throw new Exception("Keys for user " + username + " not found!");
    }
}
