package com.smart.core.centralized.wallet.generalledger.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DecodedJWTToken {

    public String productName;
    public String productCode;
    public String email;
    public String status;

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    /*public static DecodedJWTToken getDecoded(String encodedToken) throws UnsupportedEncodingException {
        String token = encodedToken.substring(AUTHENTICATION_SCHEME.length()).trim();
        String[] pieces = token.split("\\.");
        String b64payload = pieces[1];
        String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
        return new Gson().fromJson(jsonString, DecodedJWTToken.class);
    }*/
    public static DecodedJWTToken getDecoded(String encodedToken) throws UnsupportedEncodingException {
        if (encodedToken == null) {
            throw new IllegalArgumentException("Authorization token is null");
        }

        String raw = encodedToken.trim();
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("Authorization token is empty");
        }

        // Accept both "Bearer <jwt>" and "<jwt>"
        String scheme = AUTHENTICATION_SCHEME; // e.g. "Bearer"
        String token = raw;

        // Handle "Bearer" vs "Bearer " safely
        if (scheme != null && !scheme.trim().isEmpty()) {
            String schemeTrim = scheme.trim(); // "Bearer"
            if (token.regionMatches(true, 0, schemeTrim, 0, schemeTrim.length())) {
                token = token.substring(schemeTrim.length()).trim(); // removes "Bearer" and spaces
            }
        }

        if (token.isEmpty()) {
            throw new IllegalArgumentException("Bearer token is missing");
        }

        String[] pieces = token.split("\\.");
        if (pieces.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format (expected header.payload.signature)");
        }

        String b64payload = pieces[1];

        // JWT uses Base64URL (not standard Base64). Apache Base64 often works,
        // but Base64URL is safer:
        byte[] decoded = java.util.Base64.getUrlDecoder().decode(b64payload);
        String jsonString = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);

        return new Gson().fromJson(jsonString, DecodedJWTToken.class);
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

}
