package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "타임캡슐 내용 생성시 요청 본문의 JSON 데이터 구조",
        requiredProperties = {"content"}
)
public class TimeCapsuleContentRequest {
    @Schema(description = "텍스트 내용", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String content;
    //private List<MultipartFile> attachedFiles;
}
