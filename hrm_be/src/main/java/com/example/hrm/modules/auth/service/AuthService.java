package com.example.hrm.modules.auth.service;

import com.example.hrm.modules.user.dto.request.ActivateAccountRequest;
import com.example.hrm.modules.auth.dto.request.LoginRequest;
import com.example.hrm.modules.auth.dto.response.LoginResponse;
import com.example.hrm.modules.user.dto.response.UserAccountResponse;
import com.example.hrm.shared.enums.TokenType;
import com.example.hrm.shared.enums.UserStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.user.mapper.RoleMapper;
import com.example.hrm.modules.user.mapper.UserAccountMapper;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserAccountMapper userAccountMapper;
    private final RoleMapper roleMapper;

    private static final String REFRESH_KEY_PREFIX = "refreshToken:";
    private static final String USER_TOKENS_PREFIX = "user:";

    /* ================= LOGIN ================= */
    public LoginResponse login(LoginRequest request)
            throws JOSEException, ParseException {

        var user = userAccountRepository
                .findByUsernameAndIsDeletedFalse(request.getUsername())
                .orElseThrow(() ->
                        new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD, 401)
                );

        if (user.getStatus() != UserStatus.ACTIVE)
            throw new AppException(ErrorCode.USER_NOT_ACTIVE, 409);

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD, 401);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        JWTClaimsSet refreshClaims = jwtService.verifyAndParse(refreshToken);
        jwtService.assertType(refreshClaims, TokenType.REFRESH);

        // Lưu refresh token vào Redis với TTL qua helper
        storeRefreshToken(refreshClaims.getJWTID(), user.getUsername(), refreshClaims);

        String roles = jwtService.verifyAndParse(accessToken).getStringClaim("scope");

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .build();
    }

    /* ================= LOGOUT 1 TOKEN ================= */
    public void logout(String refreshToken) throws ParseException {
        JWTClaimsSet claims = jwtService.verifyAndParse(refreshToken);
        jwtService.assertRefreshToken(claims);

        String jwtId = claims.getJWTID();
        String username = claims.getSubject();

        // Xóa token cũ
        jwtService.revokeRefreshToken(jwtId, username);
    }

    /* ================= LOGOUT ALL TOKEN USER ================= */
    public void logoutAll(String refreshToken) throws ParseException {
        JWTClaimsSet claims = jwtService.verifyAndParse(refreshToken);
        jwtService.assertRefreshToken(claims);
        String username = claims.getSubject();
        jwtService.revokeAllTokens(username);
    }

    /* ================= REFRESH TOKEN ================= */
    public LoginResponse refreshToken(String refreshToken) throws JOSEException, ParseException {

        JWTClaimsSet claims = jwtService.verifyAndParse(refreshToken);
        jwtService.assertRefreshToken(claims);

        String oldJwtId = claims.getJWTID();
        String username = claims.getSubject();

        // Xóa token cũ
        redisTemplate.delete(REFRESH_KEY_PREFIX + oldJwtId);
        redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + username + ":tokens", oldJwtId);

        var user = userAccountRepository
                .findByUsernameAndIsDeletedFalseAndStatus(username, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND, 404)
                );

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        JWTClaimsSet newRefreshClaims = jwtService.verifyAndParse(newRefreshToken);

        // Lưu refresh token mới vào Redis với TTL qua helper
        storeRefreshToken(newRefreshClaims.getJWTID(), username, newRefreshClaims);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /* ================= ACTIVE ACCOUNT ================= */
    public UserAccountResponse activeAccount(ActivateAccountRequest request) throws ParseException {
        var claims = jwtService.verifyAndParse(request.getToken());
        jwtService.assertActivationToken(claims);

        var user = userAccountRepository.findByUsernameAndIsDeletedFalseAndStatus(
                        claims.getSubject(), UserStatus.PENDING_ACTIVE)
                .orElseThrow(
                        () -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        // Update password nếu FE truyền
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setStatus(UserStatus.ACTIVE);
        userAccountRepository.save(user);

        var response = userAccountMapper.toResponse(user);
        response.setStatus(user.getStatus().name());
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);
        log.info("User {} activated successfully", user.getUsername());
        return response;
    }

    /* ================= HELPER STORE REFRESH TOKEN ================= */
    private void storeRefreshToken(String jwtId, String username, JWTClaimsSet refreshClaims) {
        // Tính số giây còn lại đến khi token hết hạn
        long secondsToExpire = (refreshClaims.getExpirationTime().getTime() - System.currentTimeMillis()) / 1000;

        // Lưu token vào Redis với TTL
        redisTemplate.opsForValue().set(
                REFRESH_KEY_PREFIX + jwtId,
                username,
                Duration.ofSeconds(secondsToExpire)
        );

        // Thêm jwtId vào set user:{username}:tokens
        redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + username + ":tokens", jwtId);
    }
}
