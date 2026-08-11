package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleUpdateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
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
        description = "타임캡슐 정보 수정 응답 DTO",
        requiredProperties = {"title", "description", "mainImageUrl"}
)
public class TimeCapsuleUpdateResDto {

    @Schema(description = "타임캡슐 제목")
    private String title;

    @Schema(description = "타임캡슐 설명")
    private String description;

    @Schema(description = "타임캡슐 대표 이미지 URL")
    private String mainImageUrl;


    public static TimeCapsuleUpdateResDto toDto(TimeCapsule timeCapsule) {
        if (timeCapsule == null) {
            return null;
        }else {
            return TimeCapsuleUpdateResDto.builder()
                    .title(timeCapsule.getTitle())
                    .description(timeCapsule.getDescription())
                    .mainImageUrl(timeCapsule.getMainImage().getFileUrl())
                    .build();
        }
    }
}
