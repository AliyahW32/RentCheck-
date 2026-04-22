package com.rentcheckme.backend.service;

import com.rentcheckme.backend.model.PasswordProfile;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordProfile hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return new PasswordProfile(
            Base64.getEncoder().encodeToString(salt),
            deriveHash(password, salt, ITERATIONS),
            ITERATIONS,
            ALGORITHM
        );
    }

    public boolean matches(String password, PasswordProfile profile) {
        if (password == null || profile == null) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(profile.getSalt());
        String candidate = deriveHash(password, salt, profile.getIterations());
        return candidate.equals(profile.getHash());
    }

    private String deriveHash(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not hash password.", exception);
        }
    }
}
