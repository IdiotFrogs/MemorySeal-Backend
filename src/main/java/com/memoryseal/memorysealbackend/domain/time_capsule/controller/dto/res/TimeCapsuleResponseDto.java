package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "타임캡슐 상세 정보 응답 DTO",
        requiredProperties = {"title", "description", "createdAt", "buriedAt", "openedAt", "mainImaeUrl", "timeCapsuleActiveStatus", "userRole", "myContentCount", "myImageCount"}
)
public class TimeCapsuleResponseDto {

    @Schema(description = "타임캡슐 제목")
    private String title;

    @Schema(description = "타임캡슐 설명")
    private String description;

    @Schema(description = "타임캡슐 생성 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdAt;

    @Schema(description = "타임캡슐 묻히는 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate buriedAt;

    @Schema(description = "타임캡슐 열리는 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate openedAt;

    @Schema(description = "타임캡슐 대표 이미지 URL")
    private String mainImageUrl;

    @Schema(description = "타임캡슐 상태", examples = {"OPENED", "BURIED", "BEFOREBURIED"})
    private TimeCapsuleStatus timeCapsuleStatus;

    @Schema(description = "조회한 유저의 공동작업자 역할", examples = {"HOST", "CONTRIBUTOR"})
    private ContributorRole userRole;

    @Schema(description = "작성한 메세지 개수")
    private int myContentCount;

    @Schema(description = "작성한 이미지 개수")
    private int myImageCount;

}
