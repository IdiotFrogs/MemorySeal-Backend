package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "타임캡슐 생성 요청 DTO",
        requiredProperties = {"title", "description", "openedAt"})
public class TimeCapsuleCreateDto {

    @Schema(description = "타임캡슐 제목")
    private String title;

    @Schema(description = "타임캡슐 설명")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    @Schema(description = "타임캡슐 열리는 날짜")
    private LocalDateTime openedAt;

}
