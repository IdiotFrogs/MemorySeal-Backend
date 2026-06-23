package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.file.dto.res.AttachedFileResDto;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(
        description = "내 타임캡슐 내용 조회 응답 DTO",
        requiredProperties = {"contentId", "content", "attachedFiles"}
)
public class MyTimeCapsuleContentResDto {
    @Schema(description = "타임캡슐 내용 ID")
    private Long contentId;

    @Schema(description = "타임캡슐 내용 텍스트", nullable = true)
    private String content;

    @Schema(description = "첨부파일 목록", nullable = true)
    private List<AttachedFileResDto> attachedFiles;

    public static MyTimeCapsuleContentResDto toDto(TimeCapsuleContent timeCapsuleContent) {
        if (timeCapsuleContent == null) {
            return null;
        } else {
            List<AttachedFileResDto> attachedFiles = timeCapsuleContent.getAttachedFiles().isEmpty()
                    ? null
                    : timeCapsuleContent.getAttachedFiles().stream()
                        .map(AttachedFileResDto::toDto)
                        .toList();

            return MyTimeCapsuleContentResDto.builder()
                    .contentId(timeCapsuleContent.getId())
                    .content(timeCapsuleContent.getContent())
                    .attachedFiles(attachedFiles)
                    .build();
        }
    }
}
