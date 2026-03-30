package com.memoryseal.memorysealbackend.global.oauth.apple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {
    private String teamId;
    private String keyId;
    private String clientId;
    private String privateKeyPath;
    private Client client;

    @Getter
    @Setter
    public static class Client {
        private List<String> ids;
    }
}
