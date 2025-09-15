package com.memoryseal.memorysealbackend.global.aws.controller;

import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import com.memoryseal.memorysealbackend.global.aws.targetType.TargetType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "Aws", description = "image upload")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload/main-image")
    public ResponseEntity<String> uploadMainImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("timeCapsuleId") Long timeCapsuleId) {
        try {
            String fileUrl = s3Service.uploadImage(file, timeCapsuleId);
            return new ResponseEntity<>("타임캡슐 대표 이미지 업로드 성공. URL: " + fileUrl, HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("타임캡슐 대표 이미지 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            String fileUrl = s3Service.uploadProfileImage(file, userId);
            return new ResponseEntity<>("프로필 이미지 업로드 성공. URL: " + fileUrl, HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("프로필 이미지 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
