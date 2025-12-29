package com.pmtool.backend.DTO;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import io.jsonwebtoken.SignatureAlgorithm; // Make sure this import is correct for your jjwt version

public class KeyGenerator { // You can delete this class after use
    public static void main(String[] args) {
        // Generates a secure random key for HS256 algorithm
        String secretString = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        System.out.println("Generated JWT Secret Key: " + secretString);
    }
}

