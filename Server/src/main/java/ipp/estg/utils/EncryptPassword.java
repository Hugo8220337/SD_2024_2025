package ipp.estg.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptPassword {

    // Método para hashear uma password usando SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes); // Retorna o hash codificado em Base64
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao hashear a password: " + e.getMessage(), e);
        }
    }

    // Método para verificar uma password com base no hash armazenado
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }


}
