package com.memoryseal.memorysealbackend.domain.invite.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequest;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequestStatus;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "공동작업자 요청 목룍 응답 DTO", requiredProperties = {"requestId", "userId", "nickname", "profileImageUrl", "status"})
public class ContributorRequestResDto {
    @Schema(description = "공동작업자 요청 ID")
    private Long requestId;

    @Schema(description = "유저 ID")
    private Long userId;

    @Schema(description = "유저 닉네임")
    private String nickname;

    @Schema(description = "유저 프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "공동작업자 요청 상태", examples = {"PENDING", "APPROVED", "REJECTED"})
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
