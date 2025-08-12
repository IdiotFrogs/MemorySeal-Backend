package com.memoryseal.memorysealbackend.global.oauth.data;
import com.memoryseal.memorysealbackend.domain.user.entity.SocialType;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profile,
        SocialType socialType
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "apple" -> ofApple(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth 제공: " + registrationId);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .socialType(SocialType.GOOGLE)
                .build();
    }

    private static OAuth2UserInfo ofApple(Map<String, Object> attributes) {
        Map<String, Object> nameAttributes = (Map<String, Object>) attributes.get("name");
        String firstName = null;
        String lastName = null;
        if(nameAttributes != null) {
            firstName = (String) nameAttributes.get("firstName");
            lastName = (String) nameAttributes.get("lastName");
        }

        String name = null;
        if(firstName != null && lastName != null) {
            name = firstName + " " + lastName;
        }else if(firstName != null) {
            name = firstName;
        }else if(lastName != null) {
            name = lastName;
        }
        String email = (String) attributes.get("email");
        return OAuth2UserInfo.builder()
                .name(name)
                .email(email)
                .profile(null)
                .socialType(SocialType.APPLE)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .nickname(name)
                .email(email)
                .profileUrl(profile)
                .userActiveStatus(true)
                .role(Role.USER)
                .build();

    }
}
