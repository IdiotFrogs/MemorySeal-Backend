package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "투표 API 응답 DTO", requiredProperties = {"timeCapsuleId", "status", "contributors"})
public class BuryResponseDto {
    @Schema(description = "타임캡슐 ID")
    private Long timeCapsuleId;

    @Schema(description = "타임캡슐 상태", examples = {"OPENED", "BURIED", "BEFOREBURIED"})
    private TimeCapsuleStatus status;

    @Schema(description = "공동작업자")
    private List<ContributorBuryDto> contributors;
}
