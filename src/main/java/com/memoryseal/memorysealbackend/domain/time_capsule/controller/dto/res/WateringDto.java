package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "날짜별 물주기 정보 DTO")
public class WateringDto {

    @Schema(description = "날짜")
    private LocalDate wateredDate;

    @Schema(description = "물주기 여부")
    private Boolean isWatered;

    @Schema(description = "물을 준 유저 ID")
    private Long userId;

    @Schema(description = "물을 준 유저 프로필 URL")
    private String profileImageUrl;
}
