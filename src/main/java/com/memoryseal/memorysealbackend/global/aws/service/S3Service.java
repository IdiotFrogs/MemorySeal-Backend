package com.memoryseal.memorysealbackend.global.aws.service;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.entity.FileType;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.ContentJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.aws.targetType.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ContentJpaRepository contentJpaRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;


    @Transactional
    public String uploadImage(MultipartFile file, Long timeCapsuleId) throws IOException {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("empty");
        }
        
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("대표 이미지는 이미지 파일만 가능합니다.");
        }

        String folderPrefix = "image/main/";
        String fileName = folderPrefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = uploadToS3(file, fileName, contentType);

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(timeCapsuleId)
                .orElseThrow(() -> new IllegalArgumentException("타임캡슐을 찾을 수 없음"));

        if(timeCapsule.getMainImage() != null) {
            deleteFileFromS3(timeCapsule.getMainImage().getFileUrl());
        }

        AttachedFile newFile = AttachedFile.builder()
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(FileType.IMAGE)
                .isMain(true)
                .build();

        timeCapsule.setMainImage(newFile);
        timeCapsuleJpaRepository.save(timeCapsule);

        return fileUrl;
    }

    @Transactional
    public String uploadContentFile(MultipartFile file, Long contentId) throws IOException {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("empty");
        }

        String contentType = file.getContentType();
        String folderPrefix;
        FileType fileType;
        if(contentType != null && contentType.startsWith("image/")) {
            folderPrefix = "image/content/";
            fileType = FileType.IMAGE;
        }else if(contentType != null && contentType.startsWith("audio/")) {
            folderPrefix = "audio/content/";
            fileType = FileType.VOICERECORDED;
        }else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식: " + contentType);
        }

        String fileName = folderPrefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = uploadToS3(file, fileName, contentType);

        TimeCapsuleContent timeCapsuleContent = contentJpaRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("타임캡슐 내용을 찾을 수 없음"));

        AttachedFile newFile = AttachedFile.builder()
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(fileType)
                .isMain(false)
                .timeCapsuleContent(timeCapsuleContent)
                .build();

        timeCapsuleContent.getAttachedFiles().add(newFile);
        contentJpaRepository.save(timeCapsuleContent);

        return fileUrl;
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있음");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("프로필은 이미지 파일만 가능함");
        }

        String folderPrefix = "image/profile/";
        String fileName = folderPrefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = uploadToS3(file, fileName, contentType);

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(user.getProfileImage() != null) {
            deleteFileFromS3(user.getProfileImage().getFileUrl());
        }

        AttachedFile newFile = AttachedFile.builder()
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(FileType.IMAGE)
                .build();

        user.setProfileImage(newFile);
        userJpaRepository.save(user);

        return fileUrl;
    }

    private String uploadToS3(MultipartFile file, String fileName, String contentType) throws IOException{
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return s3Client.utilities().getUrl(b -> b.bucket(bucket).key(fileName)).toString();
    }

    private void deleteFileFromS3(String fileUrl) {
        try {
            String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
            String key = decodedUrl.substring(decodedUrl.indexOf(".com/") + 5);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3에서 성공적으로 객체를 삭제: {}", key);
        }catch (Exception e) {
            log.error("S3 객체 삭제 실패: {}", fileUrl, e);
        }
    }
}
