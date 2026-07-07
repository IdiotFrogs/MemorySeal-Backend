package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
        requiredProperties = {"title", "description"})
public class TimeCapsuleCreateDto {

    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = 20, message = "제목은 20자 이하로 입력")
    @Schema(description = "타임캡슐 제목", maxLength = 20)
    private String title;

    @Schema(description = "타임캡슐 설명")
    private String description;

}
