package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "타임캡슐 상세 정보 응답 DTO",
        requiredProperties = {"title", "description", "buriedAt", "openedAt", "timeCapsuleActiveStatus"}
)
public class TimeCapsuleResponseDto {

    private String title;

    private String description;

    private LocalDateTime buriedAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime openedAt;

    private String mainImageUrl;

    private TimeCapsuleStatus timeCapsuleStatus;

    public static TimeCapsuleResponseDto toDto(TimeCapsule timeCapsule) {
        if (timeCapsule == null) {
            return null;
        }else {
            return TimeCapsuleResponseDto.builder()
                    .title(timeCapsule.getTitle())
                    .description(timeCapsule.getDescription())
                    .buriedAt(timeCapsule.getBuriedAt())
                    .openedAt(timeCapsule.getOpenedAt())
                    .mainImageUrl(timeCapsule.getMainImage().getFileUrl())
                    .timeCapsuleStatus(timeCapsule.getTimeCapsuleStatus())
                    .build();
        }
    }
}
