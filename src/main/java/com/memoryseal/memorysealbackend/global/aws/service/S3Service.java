package com.memoryseal.memorysealbackend.global.aws.service;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.entity.FileType;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
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

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${app.default-image-url}")
    private String defaultProfileUrl;

    @Value("${app.cloudfront-domain}")
    private String cloudfrontDomain;

    @Transactional
    public AttachedFile uploadImage(MultipartFile file, Long timeCapsuleId) throws IOException {
        if(file.isEmpty()) {
            throw new AuthException(ErrorCode.EMPTY_FILE);
        }
        
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new AuthException(ErrorCode.INVALID_FILE_FORMAT);
        }

        String folderPrefix = "image/main/";
        String fileName = folderPrefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = uploadToS3(file, fileName, contentType);

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(timeCapsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

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

        return newFile;
    }

    @Transactional
    public AttachedFile uploadContentFile(MultipartFile file) throws IOException {
        if(file.isEmpty()) {
            throw new AuthException(ErrorCode.EMPTY_FILE);
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
            throw new AuthException(ErrorCode.INVALID_FILE_FORMAT);
        }

        String fileName = folderPrefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = uploadToS3(file, fileName, file.getContentType());

        AttachedFile newFile = AttachedFile.builder()
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(fileType)
                .isMain(false)
                .build();

        return newFile;
    }

    @Transactional
    public AttachedFile uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        if(file.isEmpty()) {
            throw new AuthException(ErrorCode.EMPTY_FILE);
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new AuthException(ErrorCode.INVALID_FILE_FORMAT);
        }

        String folderPrefix = "image/profile/";
        String fileName = folderPrefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = uploadToS3(file, fileName, contentType);

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if(user.getProfileImage() != null) {
            String currentUrl = user.getProfileImage().getFileUrl();
            if (!defaultProfileUrl.equals(currentUrl)) {
                deleteFileFromS3(currentUrl);
            }
        }

        AttachedFile newFile = AttachedFile.builder()
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(FileType.IMAGE)
                .build();

        user.setProfileImage(newFile);

        return newFile;
    }

    private String uploadToS3(MultipartFile file, String fileName, String contentType) throws IOException{
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        //return s3Client.utilities().getUrl(b -> b.bucket(bucket).key(fileName)).toString();
        return cloudfrontDomain + "/" + fileName;
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
