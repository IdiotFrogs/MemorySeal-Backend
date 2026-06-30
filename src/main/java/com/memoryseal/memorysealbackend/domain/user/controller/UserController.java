package com.memoryseal.memorysealbackend.domain.user.controller;

import com.memoryseal.memorysealbackend.domain.user.controller.dto.res.UserDetailResponseDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.res.UserResponseDto;
import com.memoryseal.memorysealbackend.domain.user.service.UserService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class    UserController {
    private final UserService userService;

    /*
    @PostMapping("/create")
    public void createUser(@RequestBody UserCreateDto userCreateDTO) {
        User user = userService.createUser(userCreateDTO);
    }
     */


    @PatchMapping(value = "/sign-up", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "1. 지원하지 않는 파일 형식 \t\n 2. 업로드할 파일이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "지원하지 않는 파일 형식", value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/users/sign-up\"}"),
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/users/sign-up\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/users/sign-up\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/users/sign-up\"}"))),
            @ApiResponse(responseCode = "409", description = "1. 이미 온보딩 완료된 사용자 \t\n 2. 이미 사용중인 닉네임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "이미 온보딩 완료된 사용자", value = "{\"status\": \"409\", \"error\": \"ALREADY_ONBOARDED\", \"message\": \"이미 온보딩이 완료된 유저입니다.\", \"path\": \"/users/sign-up\"}"),
                            @ExampleObject(name = "이미 사용중인 닉네임", value = "{\"status\": \"409\", \"error\": \"DUPLICATE_NICKNAME\", \"message\": \"이미 사용 중인 닉네임입니다.\", \"path\": \"/users/sign-up\"}")
                    }))
    })
    @Operation(summary = "온보딩")
    public ResponseEntity<UserResponseDto> signUpUser(
            @Parameter(
                    description = "변경할 닉네임",
                    required = true
            )
            @RequestParam("nickname")String nickname,
            @Parameter(
                    description = "업로드할 프로필 이미지 파일",
                    required = false
            )
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        UserResponseDto response = userService.signUpUser(nickname, profileImage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "유저 조회")
    public UserResponseDto getDetail(
            @Parameter(description = "조회할 유저 ID", required = true)
            @PathVariable Long userId) {
        return userService.getDetail(userId);
    }

    @GetMapping("/me")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/users/me\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/users/me\"}")))
    })
    @Operation(summary = "로그인된 프로필 조회")
    public UserDetailResponseDto getDetail() {
        return userService.getMyDetail();
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/users/{userId}\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 사용중인 닉네임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "이미 사용중인 닉네임", value = "{\"status\": \"409\", \"error\": \"DUPLICATE_NICKNAME\", \"message\": \"이미 사용 중인 닉네임입니다.\", \"path\": \"/users/{userId}\"}")))
    })
    @Operation(summary = "유저 정보 수정", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = false))
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "수정할 유저 ID")
            @PathVariable Long userId,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile file) throws IOException {
        log.info("유저 정보 수정 DTO: {}", nickname);
        UserResponseDto response = userService.updateUser(userId, nickname, file);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "1. 업로드할 파일이 없음 \t\n 2. 지원하지 않는 파일 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/users/me\"}"),
                            @ExampleObject(name = "지원하지 않는 파일 형식", value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/users/me\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/users/me\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/users/me\"}")))
    })
    @Operation(summary = "로그인된 유저 정보 수정", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = false))
    public ResponseEntity<UserResponseDto> updateMyDetail(
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile file,
            @RequestParam(value = "resetProfileImage", required = false, defaultValue = "false") Boolean resetProfileImage) throws IOException {
        UserResponseDto response = userService.updateMyDetail(nickname, file, resetProfileImage);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/users/me\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/users/me\"}"))),
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdrawUser() {
        userService.withdrawUser();
        return ResponseEntity.noContent().build();
    }
}
