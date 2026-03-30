package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleUpdateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleCreateResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleNameDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
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
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}")))
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
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}\"}")))
    })
    @PutMapping("/{capsuleId}")
    public ResponseEntity<TimeCapsuleUpdateDto> updateTimeCapsule(
            @Parameter(description = "수정할 타임캡슐 ID", required = true)
            @PathVariable Long capsuleId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정 정보 DTO", required = true)
            @RequestBody TimeCapsuleUpdateDto timeCapsuleUpdateDto) {
        TimeCapsuleUpdateDto dto = timeCapsuleService.updateTimeCapsule(capsuleId, timeCapsuleUpdateDto);
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
    public  ResponseEntity<List<TimeCapsuleNameDto>> getMyTimeCapsule() {
        List<TimeCapsuleNameDto> myTimeCapsule = timeCapsuleService.getTimeCapsule();
        return ResponseEntity.ok(myTimeCapsule);
    }
}
