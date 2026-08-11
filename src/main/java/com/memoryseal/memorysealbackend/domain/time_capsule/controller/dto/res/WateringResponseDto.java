package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.PageResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "물주기 현황 응답 DTO")
public class WateringResponseDto {

    @Schema(description = "전체 일수")
    private long totalDays;

    @Schema(description = "현재 까지 물준 횟수")
    private long wateringCount;

    @Schema(description = "물주기 성장 단계 (1~5단계)")
    private int stage;

    @Schema(description = "날짜별 물주기 목록 (묻은 날 부터 오늘까지)")
    private PageResponseDto<WateringDto> waterings;
}
