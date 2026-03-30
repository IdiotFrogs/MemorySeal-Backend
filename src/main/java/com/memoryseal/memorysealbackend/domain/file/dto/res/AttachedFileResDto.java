package com.memoryseal.memorysealbackend.domain.file.dto.res;


import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.entity.FileType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachedFileResDto {
    private Long id;
    private String fileUrl;
    private FileType fileType;

    public static AttachedFileResDto toDto(AttachedFile file) {
        return AttachedFileResDto.builder()
                .id(file.getId())
                .fileUrl(file.getFileUrl())
                .fileType(file.getFileType())
                .build();
    }
}
