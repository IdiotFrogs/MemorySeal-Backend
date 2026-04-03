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
        requiredProperties = {"title", "description", "openedAt"}
)
public class TimeCapsuleUpdateResDto {

    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime openedAt;

    private String mainImageUrl;


    public static TimeCapsuleUpdateResDto toDto(TimeCapsule timeCapsule) {
        if (timeCapsule == null) {
            return null;
        }else {
            return TimeCapsuleUpdateResDto.builder()
                    .title(timeCapsule.getTitle())
                    .description(timeCapsule.getDescription())
                    .openedAt(timeCapsule.getOpenedAt())
                    .mainImageUrl(timeCapsule.getMainImage().getFileUrl())
                    .build();
        }
    }
}
