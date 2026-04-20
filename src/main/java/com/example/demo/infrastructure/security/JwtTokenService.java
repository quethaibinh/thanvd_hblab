package com.example.demo.infrastructure.security;

import com.example.demo.application.port.out.AccessTokenProvider;
import com.example.demo.shared.exception.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService implements AccessTokenProvider {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final byte[] secretKey;

    public JwtTokenService(
            ObjectMapper objectMapper,
            Clock clock,
            @Value("${app.security.jwt.secret}") String secret) {
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String issueToken(String userId, String email, String role, Instant issuedAt, Instant expiresAt) {
        try {
            String headerJson = objectMapper.writeValueAsString(Map.of("alg", "HS256", "typ", "JWT"));
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", userId);
            payload.put("email", email);
            payload.put("role", role);
            payload.put("iat", issuedAt.getEpochSecond());
            payload.put("exp", expiresAt.getEpochSecond());

            String encodedHeader = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String signature = sign(encodedHeader + "." + encodedPayload);
            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to issue JWT access token", exception);
        }
    }

    public AuthenticatedUserPrincipal parseAndValidate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new UnauthorizedException("Invalid access token");
            }

            String signingInput = parts[0] + "." + parts[1];
            String expectedSignature = sign(signingInput);
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                throw new UnauthorizedException("Invalid access token signature");
            }

            Map<String, Object> payload = objectMapper.readValue(base64UrlDecode(parts[1]), MAP_TYPE);
            long exp = ((Number) payload.get("exp")).longValue();
            if (Instant.ofEpochSecond(exp).isBefore(Instant.now(clock))) {
                throw new UnauthorizedException("Access token has expired");
            }

            return new AuthenticatedUserPrincipal(
                    payload.get("sub").toString(),
                    payload.get("email").toString(),
                    payload.get("role").toString());
        } catch (UnauthorizedException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UnauthorizedException("Invalid access token");
        }
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
        return base64UrlEncode(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64UrlEncode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] base64UrlDecode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
