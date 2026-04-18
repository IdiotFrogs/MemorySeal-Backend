package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
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
        description = "생성된 타임캡슐 DTO",
        requiredProperties = {"id", "title", "description", "openedAt", "timeCapsuleStatus", "mainImageUrl"}
)
public class TimeCapsuleCreateResDto {
    @Schema(description = "타임캡슐 ID")
    private Long id;

    @Schema(description = "타임캡슐 ID")
    private String title;

    @Schema(description = "타임캡슐 설명")
    private String description;

    @Schema(description = "타임캡슐 열리는 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime openedAt;

    @Schema(description = "타임캡슐 상태", examples = {"OPENED", "BURIED", "BEFOREBURIED"})
    private TimeCapsuleStatus timeCapsuleStatus;

    @Schema(description = "타임캡슐 대표 이미지 URL")
    private String mainImageUrl;

    public static TimeCapsuleCreateResDto toDto(TimeCapsule timeCapsule) {
        if (timeCapsule == null) {
            return null;
        }else {
            String imageUrl = null;
            if(timeCapsule.getMainImage() != null) {
                imageUrl = timeCapsule.getMainImage().getFileUrl();
            }
            return TimeCapsuleCreateResDto.builder()
                    .id(timeCapsule.getId())
                    .title(timeCapsule.getTitle())
                    .description(timeCapsule.getDescription())
                    .openedAt(timeCapsule.getOpenedAt())
                    .timeCapsuleStatus(timeCapsule.getTimeCapsuleStatus())
                    .mainImageUrl(imageUrl)
                    .build();
        }
    }


}
