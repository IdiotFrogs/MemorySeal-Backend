package com.memoryseal.memorysealbackend.global.oauth.apple;

import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleAuthClient {
    private final AppleProperties appleProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public AppleTokenResponse getAppleToken(String authorizationCode) throws Exception {
        String clientSecret = createClientSecret();

        String tokenUrl = "https://appleid.apple.com/auth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.getClientId());
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.postForObject(tokenUrl, request, AppleTokenResponse.class);
    }

    public JWTClaimsSet verifyIdToken(String idTokenString) throws Exception {
        URL jwksUrl = new URL("https://appleid.apple.com/auth/keys");
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder
                .create(jwksUrl)
                .cache(true)
                .rateLimited(true)
                .build();

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);

        DefaultJWTClaimsVerifier<SecurityContext> claimsVerifier =
                new DefaultJWTClaimsVerifier<>(
                        new JWTClaimsSet.Builder()
                                .issuer("https://appleid.apple.com")
                                .build(),
                        new HashSet<>(Arrays.asList("sub", "email", "iat", "exp"))
                );
        jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);

        JWTClaimsSet claims = jwtProcessor.process(idTokenString, null);

        List<String> audience = claims.getAudience();
        List<String> allowedClientIds = appleProperties.getClient().getIds();

        log.info("토큰 audience: {}", audience);
        log.info("허용된 cliendIds: {}", appleProperties.getClient().getIds());

        boolean isValidAudience = audience.stream()
                .anyMatch(allowedClientIds::contains);

        if(!isValidAudience) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        return claims;
    }

    public String createClientSecret() throws Exception {
        Date now = new Date();

        Date expiration = new Date(now.getTime() + (1000 * 60 * 5));

        PrivateKey privateKey = getPrivateKey();

        return Jwts.builder()
                .setHeaderParam("kid", appleProperties.getKeyId())
                .setIssuer(appleProperties.getTeamId())
                .setAudience("https://appleid.apple.com")
                .setSubject(appleProperties.getClientId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }
    
    private PrivateKey getPrivateKey() throws Exception {
        Resource resource = new DefaultResourceLoader().getResource(appleProperties.getPrivateKeyPath());
        String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        String privateKeyPEM = content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decode = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decode));
    }

    public void revokeAppleToken(String appleRefreshToken) throws Exception {
        String clientSecret = createClientSecret();

        String revokeUrl = "https://appleid.apple.com/auth/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.getClientId());
        params.add("client_secret", clientSecret);
        params.add("token", appleRefreshToken);
        params.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        restTemplate.postForObject(revokeUrl, request, String.class);
    }

}
