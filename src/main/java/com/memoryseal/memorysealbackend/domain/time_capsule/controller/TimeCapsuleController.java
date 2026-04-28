package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleUpdateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleCreateResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleNameDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleUpdateResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.service.TimeCapsuleService;
import com.memoryseal.memorysealbackend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
@Tag(name = "Time Capsule")
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @Operation(summary = "타임캡슐 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "1. 지원하지 않는 파일 형식 \t\n 2. 업로드할 파일이 없음 \t\n 3. openedAt이 현재보다 과거임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "지원하지 않는 파일 형식", value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"),
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"),
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"INVALID_OPENED_AT\", \"message\": \"openedAt이 현재보다 과거입니다.\", \"path\": \"/time-capsules/{capsuleId}\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/time-capsules/create\"}"))),
    })
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TimeCapsuleCreateResDto> createTimeCapsule(
            //@ModelAttribute @Valid TimeCapsuleCreateDto timeCapsuleCreateDto,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("timeCapsuleCreateDto")
            @Valid TimeCapsuleCreateDto timeCapsuleCreateDto,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "mainImage", required = true)
            MultipartFile mainImage
    ) throws IOException {
        log.info("컨트롤러 진입 - DTO: {}", timeCapsuleCreateDto);
        TimeCapsuleCreateResDto dto = timeCapsuleService.createTimeCapsule(timeCapsuleCreateDto, mainImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "타임캡슐 상세조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"))),
    })
    @GetMapping("/{capsuleId}")
    public ResponseEntity<TimeCapsuleResponseDto> getDetail(
            @Parameter(description = "조회할 타임캡슐 ID", required = true)
            @PathVariable Long capsuleId) {
        TimeCapsuleResponseDto dto = timeCapsuleService.getDetail(capsuleId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "타임캡슐 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "1. 지원하지 않는 파일 형식 \t\n 2. 업로드할 파일이 없음 \t\n 3. openedAt이 현재보다 과거임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "지원하지 않는 파일 형식", value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"),
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"),
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"INVALID_OPENED_AT\", \"message\": \"openedAt이 현재보다 과거입니다.\", \"path\": \"/time-capsules/{capsuleId}\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"))),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 묻힌 타임캡슐임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"409\", \"error\": \"ALREADY_BURIED\", \"message\": \"이미 묻힌 타임캡슐입니다.\", \"path\": \"/time-capsules/{capsuleId}\"}")))
    })
    @PutMapping(value = "/{capsuleId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TimeCapsuleUpdateResDto> updateTimeCapsule(
            @Parameter(description = "수정할 타임캡슐 ID", required = true)
            @PathVariable Long capsuleId,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart(value = "timeCapsuleUpdateDto", required = false)
            @Valid TimeCapsuleUpdateDto timeCapsuleUpdateDto,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "mainImage", required = false)
            MultipartFile mainImage) throws IOException {
        TimeCapsuleUpdateResDto dto = timeCapsuleService.updateTimeCapsule(capsuleId, timeCapsuleUpdateDto, mainImage);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "내 타임캡슐 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/time-capsules/my\"}"))),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/my\"}")))
    })
    @GetMapping("/my")
    public ResponseEntity<List<TimeCapsuleNameDto>> getMyTimeCapsule() {
        List<TimeCapsuleNameDto> myTimeCapsule = timeCapsuleService.getTimeCapsule();
        return ResponseEntity.ok(myTimeCapsule);
    }

    @Operation(summary = "타임캡슐 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}"))),
            @ApiResponse(responseCode = "404", description = "타임캡슐 내용을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "타임캡슐 내용을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"CONTENT_NOT_FOUND\", \"message\": \"타임캡슐을 내용을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}")))
    })
    @DeleteMapping("/{capsuleId}")
    public ResponseEntity<Void> deleteTimeCapsule(@PathVariable Long capsuleId) {
        timeCapsuleService.deleteCapsule(capsuleId);
        return ResponseEntity.ok().build();
    }
}
