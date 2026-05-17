package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "공동작업자 상세 정보 DTO",
        requiredProperties = {"contributorRole", "nickname", "bury", "userId", "profileImageUrl", "userActiveStatus", "isMe"}
)
public class ContributorResponseDto {

    @Schema(description = "공동작업자 역할", examples = {"HOST", "CONTRIBUTOR"})
    private ContributorRole contributorRole;

    @Schema(description = "유저 닉네임")
    private String nickname;

    @Schema(description = "유저 ID")
    private Long userId;

    @Schema(description = "유저 프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "계정 활성화 여부(true: 활동 중, false: 탈퇴)")
    private Boolean userActiveStatus;

    @Schema(description = "조회한 유저 확인용 컬럼")
    private Boolean isMe;

    /*
    public static ContributorResponseDto toDto(Contributor contributor) {
        if (contributor == null) {
            return null;
        }else {
            return ContributorResponseDto.builder()
                    .contributorRole(contributor.getContributorRole())
                    .bury(contributor.getBury())
                    .userId(contributor.getUserId())
                    .build();
        }
    }
     */
}
