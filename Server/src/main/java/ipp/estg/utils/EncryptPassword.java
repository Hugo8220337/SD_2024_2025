package ipp.estg.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for hashing passwords using SHA-256 and verifying passwords against stored hashes.
 * This class provides methods to hash a password and to verify a plain password against a hashed password.
 */
public class EncryptPassword {

    /**
     * Hashes a password using the SHA-256 algorithm and encodes the result in Base64.
     * The password is converted to a byte array using UTF-8 encoding before hashing.
     *
     * @param password The plain text password to be hashed.
     * @return A Base64 encoded string representing the hashed password.
     * @throws RuntimeException If there is an error while hashing the password.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes); // Retorna o hash codificado em Base64
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao hashear a password: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies a plain password by comparing its hash with the stored hash.
     * This method hashes the plain password and compares it with the provided hash.
     *
     * @param plainPassword The plain text password to verify.
     * @param hashedPassword The stored hashed password to compare with.
     * @return true if the hashes match, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }


}
