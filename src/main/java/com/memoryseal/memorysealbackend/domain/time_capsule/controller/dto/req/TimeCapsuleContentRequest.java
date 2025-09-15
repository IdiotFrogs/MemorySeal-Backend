package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req;

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
public class TimeCapsuleContentRequest {
    private String content;
    private Long userId;
    private List<MultipartFile> attachedFiles;
}
