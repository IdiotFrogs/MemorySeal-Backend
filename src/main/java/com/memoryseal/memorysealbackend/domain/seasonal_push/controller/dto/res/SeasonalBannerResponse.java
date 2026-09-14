package com.memoryseal.memorysealbackend.domain.seasonal_push.controller.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.Season;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "배너 조회 응답 DTO",
        requiredProperties = {"content", "season", "capsuleId"}
)
public class SeasonalBannerResponse {
    @Schema(description = "계절에 따른 배너 내용")
    private String content;
    @Schema(description = "계절")
    private Season season;
    @Schema(description = "타임캡슐 ID")
    private Long capsuleId;

    public static SeasonalBannerResponse empty() {
        return SeasonalBannerResponse.builder().build();
    }
}
