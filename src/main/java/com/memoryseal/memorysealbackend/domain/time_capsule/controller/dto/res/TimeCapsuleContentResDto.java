package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.file.dto.res.AttachedFileResDto;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Schema(
        description = "생성된 타임캡슐 내용 DTO",
        requiredProperties = {"contentId", "content", "attachedFileUrls"}
)
public class TimeCapsuleContentResDto {
    @Schema(description = "타임캡슐 내용 ID")
    private Long contentId;

    @Schema(description = "타임캡슐 내용 텍스트", nullable = true)
    private String content;

    @Schema(description = "타임캡슐 내용 이미지 URL 목록", nullable = true)
    private List<String> attachedFileUrls;

    public static TimeCapsuleContentResDto toDto(TimeCapsuleContent timeCapsuleContent) {
        if (timeCapsuleContent == null) {
            return null;
        } else {
            List<String> fileUrls = timeCapsuleContent.getAttachedFiles().isEmpty()
                    ? null
                    : timeCapsuleContent.getAttachedFiles()
                        .stream()
                        .map(AttachedFile::getFileUrl)
                        .toList();

            return TimeCapsuleContentResDto.builder()
                    .contentId(timeCapsuleContent.getId())
                    .content(timeCapsuleContent.getContent())
                    .attachedFileUrls(fileUrls)
                    .build();
        }
    }
}
