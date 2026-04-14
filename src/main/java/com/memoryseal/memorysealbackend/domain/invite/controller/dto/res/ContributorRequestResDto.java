package com.memoryseal.memorysealbackend.domain.invite.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequest;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequestStatus;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContributorRequestResDto {
    private Long requestId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private ContributorRequestStatus status;

    public static ContributorRequestResDto toDto(ContributorRequest request, User user) {
        return ContributorRequestResDto.builder()
                .requestId(request.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImage().getFileUrl())
                .status(request.getStatus())
                .build();
    }
}
