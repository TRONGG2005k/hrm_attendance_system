package com.example.hrm.shared.configuration;

import com.example.hrm.shared.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JwtKeyStore {

    private final Map<String, byte[]> keys;

    public JwtKeyStore(
            @Value("${app.jwt.signerKeyAccess}") String access,
            @Value("${app.jwt.signerKeyRefresh}") String refresh,
            @Value("${app.jwt.signerKeyActivation}") String activation
    ) {
        keys = Map.of(
                TokenType.ACCESS.name(), access.getBytes(StandardCharsets.UTF_8),
                TokenType.REFRESH.name(), refresh.getBytes(StandardCharsets.UTF_8),
                TokenType.ACTIVATION.name(), activation.getBytes(StandardCharsets.UTF_8)
        );
    }

    public byte[] getKey(String kid) {
        return keys.get(kid);
    }
}
