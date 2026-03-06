package com.example.hrm.modules.auth.service;

import com.example.hrm.shared.configuration.JwtKeyStore;
import com.example.hrm.modules.user.entity.Role;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.shared.enums.TokenType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtKeyStore keyStore;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.jwt.valid-duration}")
    private  long accessExp;

    @Value("${app.jwt.refreshable-duration}")
    private  long refreshExp;

    private static final String ISSUER = "myapp.com";

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String sameSite;
    /* ================= GENERATE ================= */

    public String generateAccessToken(UserAccount user) throws JOSEException {
        return generateToken(user, TokenType.ACCESS, accessExp);
    }

    public String generateRefreshToken(UserAccount user) throws JOSEException {
        String refreshToken = generateToken(user, TokenType.REFRESH, refreshExp);

        JWTClaimsSet claims = verifyAndParse(refreshToken);
        String jwtId = claims.getJWTID();

        // Lưu token vào Redis với TTL
        redisTemplate.opsForValue().set("refreshToken:" + jwtId, user.getUsername());
        redisTemplate.expire("refreshToken:" + jwtId, Duration.ofSeconds(refreshExp));

        // Lưu jwtId vào Set user:{username}:tokens
        redisTemplate.opsForSet().add("user:" + user.getUsername() + ":tokens", jwtId);

        return refreshToken;
    }

    public ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshExp) // dùng refreshExp luôn cho đồng bộ
                .sameSite(sameSite)
                .build();
    }

    public String generateActivationToken(UserAccount user) throws JOSEException {
        return generateToken(user, TokenType.ACTIVATION, 900);
    }

    private String generateToken(UserAccount user, TokenType type, long expSeconds)
            throws JOSEException {

        JWTClaimsSet claims = buildClaims(user, type, expSeconds);

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS512)
                        .keyID(type.name())
                        .build(),
                claims
        );

        jwt.sign(new MACSigner(keyStore.getKey(type.name())));
        return jwt.serialize();
    }

    private JWTClaimsSet buildClaims(UserAccount user, TokenType type, long expSeconds) {
        String scope = user.getRoles().stream()
                .map(Role::getName)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        return new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(ISSUER)
                .claim("scope", scope)
                .claim("type", type.name())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(expSeconds, ChronoUnit.SECONDS)))
                .build();
    }

    /* ================= VERIFY (CRYPTO) ================= */

    public JWTClaimsSet verifyAndParse(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            String kid = jwt.getHeader().getKeyID();
            byte[] key = keyStore.getKey(kid);

            if (key == null || !jwt.verify(new MACVerifier(key)))
                throw new AppException(ErrorCode.INVALID_TOKEN, 401);

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (!ISSUER.equals(claims.getIssuer()))
                throw new AppException(ErrorCode.INVALID_TOKEN, 401);

            if (claims.getExpirationTime().before(new Date()))
                throw new AppException(ErrorCode.TOKEN_HAS_EXPIRED, 401);

            return claims;

        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 401);
        }
    }

    /* ================= ASSERT PURPOSE ================= */

    public void assertAccessToken(JWTClaimsSet claims) throws ParseException {
        assertType(claims, TokenType.ACCESS);
    }

    public void assertRefreshToken(JWTClaimsSet claims) throws ParseException {
        assertType(claims, TokenType.REFRESH);

        String jwtId = claims.getJWTID();
        String username = redisTemplate.opsForValue().get("refreshToken:" + jwtId);
        if (username == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 401);
        }
    }

    public void assertActivationToken(JWTClaimsSet claims) throws ParseException {
        assertType(claims, TokenType.ACTIVATION);
    }

    void assertType(JWTClaimsSet claims, TokenType expected) throws ParseException {
        TokenType actual = TokenType.valueOf(claims.getStringClaim("type"));
        if (actual != expected)
            throw new AppException(ErrorCode.INVALID_TOKEN_TYPE, 401);
    }

    /* ================= HELPER ================= */

    public void revokeRefreshToken(String jwtId, String username) {
        // Xóa token cụ thể
        redisTemplate.delete("refreshToken:" + jwtId);
        redisTemplate.opsForSet().remove("user:" + username + ":tokens", jwtId);
    }

    public void revokeAllTokens(String username) {
        // Xóa tất cả token của user
        var jtis = redisTemplate.opsForSet().members("user:" + username + ":tokens");
        if (jtis != null) {
            for (String jti : jtis) {
                redisTemplate.delete("refreshToken:" + jti);
            }
        }
        redisTemplate.delete("user:" + username + ":tokens");
    }
}
