package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Schema(
        description = "사용자의 타임캡슐 목록 조회시 사용되는 정보 DTO",
        requiredProperties = {"timeCapsuleId", "title", "openedAt", "mainImageUrl", "timeCapsuleStatus", "role"}
)
public class TimeCapsuleNameDto {

    @Schema(description = "타임캡슐 ID")
    private Long timeCapsuleId;

    @Schema(description = "타임캡슐 제목")
    private String title;

    @Schema(description = "타임캡슐 열리는 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime openedAt;

    @Schema(description = "타임캡슐 대표 이미지 URL")
    private String mainImageUrl;

    @Schema(description = "타임캡슐 상태", examples = {"OPENED", "BURIED", "BEFOREBURIED"})
    private TimeCapsuleStatus timeCapsuleStatus;

    @Schema(description = "공동작업자 역할", examples = {"HOST", "CONTRIBUTOR"})
    private ContributorRole role;
}
