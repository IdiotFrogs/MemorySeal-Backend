package com.memoryseal.memorysealbackend.domain.contributor.service;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorJpaRepository contributorJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public List<ContributorResponseDto> getDetail(Long capsuleId) {
        List<Contributor> contributors = contributorJpaRepository.findByTimeCapsuleId(capsuleId);

        return contributors.stream()
                .map(contributor -> {
                    User user = userJpaRepository.findById(contributor.getUserId())
                            .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
                    return ContributorResponseDto.builder()
                            .userId(user.getId())
                            .nickname(user.getNickname())
                            .profileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getFileUrl() : null)
                            .userActiveStatus(user.getUserActiveStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
