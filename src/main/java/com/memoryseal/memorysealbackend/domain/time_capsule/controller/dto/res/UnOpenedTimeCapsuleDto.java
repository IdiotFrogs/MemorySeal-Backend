package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Schema(
        description = "애니메이션 확인 안 한 캡슐 응답 DTO",
        requiredProperties = {"timeCapsuleId", "title", "openedAt", "mainImageUrl"}
)
public class UnOpenedTimeCapsuleDto {
    @Schema(description = "타임캡슐 ID")
    private Long timeCapsuleId;

    @Schema(description = "타임캡슐 제목")
    private String title;

    @Schema(description = "타임캡슐 열리는 날짜")
    private LocalDate openedAt;

    @Schema(description = "타임캡슐 대표 이미지 URL")
    private String mainImageUrl;
}
