package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "공동작업자 묻기 현황 DTO", requiredProperties = {"contributorRole", "bury", "userId"})
public class ContributorBuryDto {
    @Schema(description = "공동작업자 역할", examples = {"HOST", "CONTRIBUTOR"})
    private ContributorRole contributorRole;

    @Schema(description = "묻기 동의 여부")
    private Boolean bury;

    @Schema(description = "유저 ID")
    private Long userId;
}
