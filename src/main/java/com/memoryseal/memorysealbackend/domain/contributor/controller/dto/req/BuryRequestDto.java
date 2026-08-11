package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class BuryRequestDto {
    @NotNull(message = "타임캡슐 개봉 날짜는 필수 항목 입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "타임캡슐 열리는 날짜")
    private LocalDate openedAt;
}
