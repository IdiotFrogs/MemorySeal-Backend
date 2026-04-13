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
        description = "공동작업자 상세 정보 구조",
        requiredProperties = {"contributorRole", "bury", "userId"}
)
public class ContributorResponseDto {

    private ContributorRole contributorRole;

    private String nickname;

    private Boolean bury;

    private Long userId;

    private String profileImageUrl;

    private Boolean userActiveStatus;

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
