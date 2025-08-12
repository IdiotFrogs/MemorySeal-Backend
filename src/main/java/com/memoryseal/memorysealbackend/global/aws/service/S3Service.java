package com.memoryseal.memorysealbackend.global.aws.service;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
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


    @Transactional
    public String uploadImage(MultipartFile image, TargetType targetType, Long id) throws IOException {
        if(image.isEmpty()) {
            throw new IllegalArgumentException("empty");
        }

        String folderPrefix = targetType.name().toLowerCase() + "/";
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(image.getContentType())
                .contentLength(image.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

        String imageUrl = s3Client.utilities().getUrl(b -> b.bucket(bucket).key(fileName)).toString();
        if(targetType == TargetType.PROFILE) {
            User user = userJpaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("user now found"));
            user.setProfileUrl(imageUrl);
            userJpaRepository.save(user);
        }else if(targetType == TargetType.TIME_CAPSULE) {
            TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("time-capsule now found"));
            timeCapsule.setTimeCapsuleImage(imageUrl);
            timeCapsuleJpaRepository.save(timeCapsule);
        }

        return imageUrl;
    }
}
