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
        requiredProperties = {"id", "content", "attachedFiles"}
)
public class TimeCapsuleContentResDto {
    @Schema(description = "타임캡슐 내용 ID")
    private Long id;

    @Schema(description = "타임캡슐 내용 텍스트")
    private String content;

    @Schema(description = "타임캡슐 내용 파일")
    private List<AttachedFileResDto> attachedFiles;

    public static TimeCapsuleContentResDto toDto(TimeCapsuleContent timeCapsuleContent) {
        if (timeCapsuleContent == null) {
            return null;
        } else {
            return TimeCapsuleContentResDto.builder()
                    .id(timeCapsuleContent.getId())
                    .content(timeCapsuleContent.getContent())
                    .attachedFiles(timeCapsuleContent.getAttachedFiles().stream()
                            .map(AttachedFileResDto::toDto)
                            .toList())
                    .build();
        }
    }
}
