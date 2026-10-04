package com.helpinggurus.util;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Salted PBKDF2 password hashing (passwords are never stored in plain text). */
public final class PasswordUtil {
    private PasswordUtil() {}

    /** Returns "salt:hash" (both Base64). A new random 16-byte salt is used for every password. */
    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(pbkdf2(password, salt));
    }

    /** Re-hashes the typed password with the stored salt and compares in constant time. */
    public static boolean verify(String password, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 2) return false;
        byte[] expected = Base64.getDecoder().decode(parts[1]);
        return MessageDigest.isEqual(expected, pbkdf2(password, Base64.getDecoder().decode(parts[0])));
    }

    // PBKDF2 with HmacSHA256, 65,536 iterations, 256-bit key.
    private static byte[] pbkdf2(String password, byte[] salt) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(password.toCharArray(), salt, 65536, 256)).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) { throw new IllegalStateException(e); }
    }
}
