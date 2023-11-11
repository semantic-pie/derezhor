package io.github.semanticpie.derezhor.externalAgents.users.services.encrypt;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
public class PasswordEncryptor {

    public static byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] encryptPassword(@Nonnull String password, byte[] salt){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
          log.info("No such algorithm to encrypt");
          throw new RuntimeException(ex);
        }
    }
}
