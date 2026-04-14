package com.memoryseal.memorysealbackend.domain.file.dto.res;


import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.entity.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "파일 DTO", requiredProperties = {"id", "fileUrl", "fileType"})
public class AttachedFileResDto {
    @Schema(description = "파일 ID")
    private Long id;

    @Schema(description = "파일 URL")
    private String fileUrl;

    @Schema(description = "파일 종류", examples = {"IMAGE", "VOICERECORDED"})
    private FileType fileType;

    public static AttachedFileResDto toDto(AttachedFile file) {
        return AttachedFileResDto.builder()
                .id(file.getId())
                .fileUrl(file.getFileUrl())
                .fileType(file.getFileType())
                .build();
    }
}
