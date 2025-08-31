package com.memoryseal.memorysealbackend.global.oauth.service;

import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.global.oauth.auth.AuthDetails;
import com.memoryseal.memorysealbackend.global.oauth.data.OAuth2UserInfo;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserJpaRepository userJpaRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 유저 정보 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // resistrationId 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        User user = getOrSave(oAuth2UserInfo);

        // OAuth2User로 반환
        return new AuthDetails(user, oAuth2UserAttributes, userNameAttributeName);
    }

    private User getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        User user = userJpaRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toEntity);
        return userJpaRepository.save(user);
    }

}
