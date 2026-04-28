package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "묻기 현황 확인 DTO", requiredProperties = {"memberCount", "trueCount"})
public class BuryCheckResDto {
    @Schema(description = "총 구성원 수")
    private Long memberCount;

    @Schema(description = "묻기 동의한 사람 수")
    private Long trueCount;
}
