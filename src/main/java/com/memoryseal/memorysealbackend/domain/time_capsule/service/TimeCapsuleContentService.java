package com.memoryseal.memorysealbackend.domain.time_capsule.service;

import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleContentRequest;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.ContentJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TimeCapsuleContentService {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContentJpaRepository contentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final S3Service s3Service;

    @Transactional
    public TimeCapsuleContent createContent(Long timeCapsuleId, TimeCapsuleContentRequest request) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(timeCapsuleId)
                .orElseThrow(() -> new IllegalArgumentException("타임캡슐을 찾을 수 없음"));
        User user = userJpaRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없음"));

        TimeCapsuleContent content = TimeCapsuleContent.builder()
                .content(request.getContent())
                .timeCapsule(timeCapsule)
                .user(user)
                .build();

        TimeCapsuleContent saveContent = contentJpaRepository.save(content);

        if(request.getAttachedFiles() != null && !request.getAttachedFiles().isEmpty()) {
            for(MultipartFile file : request.getAttachedFiles()) {
                try {
                    s3Service.uploadContentFile(file, saveContent.getId());
                }catch (IOException e) {
                    throw new RuntimeException("파일 업로드 중 오류가 발생함", e);
                }
            }
        }
        return saveContent;
    }
}
