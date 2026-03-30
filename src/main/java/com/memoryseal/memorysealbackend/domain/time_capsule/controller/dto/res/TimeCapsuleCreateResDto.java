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
    private Long id;

    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime openedAt;

    private TimeCapsuleStatus timeCapsuleStatus;

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
