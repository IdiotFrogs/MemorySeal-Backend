package com.memoryseal.memorysealbackend.global.aws.controller;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import com.memoryseal.memorysealbackend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class  S3Controller {
/*
    private final S3Service s3Service;

    @PostMapping(value = "/upload/main-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 파일 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/s3/upload/main-image/\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 업로드할 파일이 없음 \t\n 2. 타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"404\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/s3/upload/main-image/\"}"),
                            @ExampleObject(name = "타임캡슐을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/s3/upload/main-image/\"}")
                    }))
    })
    @Operation(summary = "타임캡슐 대표 이미지 업로드")
    public ResponseEntity<String> uploadMainImage(
            @Parameter(
                    description = "업로드할 이미지 파일",
                    required = true
            )
            @RequestParam("file") MultipartFile file,
            @Parameter(
                    description = "타임캡슐 ID",
                    required = true
            )
            @RequestParam("timeCapsuleId") Long timeCapsuleId) {
        try {
            AttachedFile newFile = s3Service.uploadImage(file, timeCapsuleId);
            return new ResponseEntity<>("타임캡슐 대표 이미지 업로드 성공. URL: " + newFile.getFileUrl(), HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("타임캡슐 대표 이미지 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 파일 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/s3/upload/profile-image/\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 업로드할 파일이 없음 \t\n 2. 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"404\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/s3/upload/profile-image/\"}"),
                            @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/s3/upload/profile-image/\"}")
                    }))
    })
    @Operation(summary = "프로필 이미지 업로드")
    public ResponseEntity<String> uploadProfileImage(
            @Parameter(
                    description = "업로드 할 이미지 파일",
                    required = true
            )
            @RequestParam("file") MultipartFile file,
            @Parameter(
                    description = "유저 ID",
                    required = true
            )
            @RequestParam("userId") Long userId) {
        try {
            AttachedFile newFile = s3Service.uploadProfileImage(file, userId);
            return new ResponseEntity<>("프로필 이미지 업로드 성공. URL: " + newFile.getFileUrl(), HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("프로필 이미지 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 */
}
