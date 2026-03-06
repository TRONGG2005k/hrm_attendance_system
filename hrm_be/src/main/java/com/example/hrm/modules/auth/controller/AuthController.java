package com.example.hrm.modules.auth.controller;

import com.example.hrm.modules.user.dto.request.ActivateAccountRequest;
import com.example.hrm.modules.auth.dto.request.LoginRequest;
import com.example.hrm.modules.auth.dto.response.LoginResponse;
import com.example.hrm.modules.user.dto.response.UserAccountResponse;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.auth.service.AuthService;
import com.example.hrm.modules.auth.service.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("${app.api-prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) throws ParseException, JOSEException {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, 400, "Username và password không được để trống");
        }
        log.warn(" System.out.println(\"LOGIN HIT\");");
        var response = authService.login(request);

        if (response == null || response.getRefreshToken() == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 500, "Lỗi tạo token");
        }

        var cookie = jwtService.createRefreshCookie(response.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(LoginResponse.builder()
                        .accessToken(response.getAccessToken())
                        .roles(response.getRoles())
                        .build());
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) throws ParseException {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token not found.");
        }
        authService.logout(refreshToken);
        // Xoá cookie trên trình duyệt
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // nếu bạn dùng HTTPS
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logout success");
    }

    @DeleteMapping("/logoutAll")
    public ResponseEntity<?> logoutAll(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) throws ParseException {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token not found.");
        }
        authService.logoutAll(refreshToken);
        // Xoá cookie trên trình duyệt
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // nếu bạn dùng HTTPS
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logout success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ){
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 400, "Refresh token không được để trống");
        }

        try {
            var response = authService.refreshToken(refreshToken);

            if (response == null || response.getRefreshToken() == null) {
                throw new AppException(ErrorCode.INVALID_TOKEN, 500, "Lỗi refresh token");
            }

            var cookie = jwtService.createRefreshCookie(response.getRefreshToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(LoginResponse.builder()
                            .accessToken(response.getAccessToken())
                            .roles(response.getRoles())
                            .build());
        }  catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 409);
        }
    }

    /**
     * Activate user account
     * Request body contains activation token and optional password
     */
    @PostMapping("/activate")
    public ResponseEntity<UserAccountResponse> activateAccount(
            @RequestBody ActivateAccountRequest request
    ) throws ParseException {
        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 400, "Token kích hoạt không được để trống");
        }

        UserAccountResponse response = authService.activeAccount(request);

        if (response == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, 500, "Lỗi kích hoạt tài khoản");
        }

        return ResponseEntity.ok(response);
    }
}
