package com.memoryseal.memorysealbackend.global.oauth.apple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;


@Component
@RequiredArgsConstructor
@Slf4j
public class AppleClientSecretGenerator {

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.private-key-path}")
    private String privateKeyPath;

    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;


}
