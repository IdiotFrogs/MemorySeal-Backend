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
        requiredProperties = {"timeCapsuleId", "title", "openedAt", "timeCapsuleStatus", "role"}
)
public class TimeCapsuleNameDto {

    private Long timeCapsuleId;

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime openedAt;

    private String mainImageUrl;

    private TimeCapsuleStatus timeCapsuleStatus;

    private ContributorRole role;
}
