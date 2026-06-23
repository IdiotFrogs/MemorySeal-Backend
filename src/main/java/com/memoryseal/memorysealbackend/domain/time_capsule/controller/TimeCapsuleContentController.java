package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.PageResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.MyTimeCapsuleContentResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleContentResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.UserContentDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.service.TimeCapsuleContentService;
import com.memoryseal.memorysealbackend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/time-capsule-content")
@RequiredArgsConstructor
@Tag(name = "Time Capsule Content")
public class TimeCapsuleContentController {
    private final TimeCapsuleContentService timeCapsuleContentService;

    @PostMapping(value = "/{timeCapsuleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "타임캡슐 내용 생성", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = false))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "400", description = "1. 내용 또는 파일 중 적어도 하나는 포함되어야 함 \t\n 2. 지원하지 않는 파일 형식 \t\n 3. 업로드할 파일이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "내용 또는 파일 중 적어도 하나는 포함되어야 함", value = "{\"status\": \"400\", \"error\": \"EMPTY_CONTENT\", \"message\": \"내용 또는 파일 중 적어도 하나는 포함되어야 합니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}\"}"),
                            @ExampleObject(name = "지원하지 않는 파일 형식", value = "{\"status\": \"400\", \"error\": \"INVALID_FILE_FORMAT\", \"message\": \"지원하지 않는 파일 형식입니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}\"}"),
                            @ExampleObject(name = "업로드할 파일이 없음", value = "{\"status\": \"400\", \"error\": \"EMPTY_FILE\", \"message\": \"업로드할 파일이 없습니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 타임캡슐을 찾을 수 없음 \t\n 2. 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "타임캡슐을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}\"}"),
                            @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}\"}")
                    }))
    })
    public ResponseEntity<TimeCapsuleContentResDto> createContent(
            @Parameter(
                    description = "타임캡슐 ID",
                    required = true
            )
            @PathVariable Long timeCapsuleId,
            @Parameter(
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE),
                    description = "텍스트 데이터 파트",
                    required = false,
                    schema = @Schema(type = "string")
            )
            @RequestPart(value = "content", required = false) String content,
            @Parameter(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE),
                    description = "파일 리스트 파트",
                    required = false,
                    array = @ArraySchema(
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        TimeCapsuleContentResDto newContent = timeCapsuleContentService.createContent(timeCapsuleId, content, files);
        return new ResponseEntity<>(newContent, HttpStatus.CREATED);
    }

    @Operation(summary = "타임캡슐 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "타임캡슐 내용을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "타임캡슐 내용을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"CONTENT_NOT_FOUND\", \"message\": \"타임캡슐을 내용을 찾을 수 없습니다.\", \"path\": \"/api/time-capsule-content/{contentId}\"}")))
    })
    @PutMapping(value = "/{contentId}")
    public ResponseEntity<TimeCapsuleContentResDto> updateTimeCapsuleContent(
            @Parameter(description = "수정할 타임캡슐 내용 ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "수정할 타임캡슐 내용", required = true)
            @RequestParam("content")String content
    ) {
        TimeCapsuleContentResDto dto = timeCapsuleContentService.updateContent(contentId, content);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "타임캡슐 전체 내용 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}/contents\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}/contents\"}")))
    })
    @GetMapping("/{timeCapsuleId}/contents")
    public ResponseEntity<PageResponseDto<UserContentDto>> getContents(
            @PathVariable Long timeCapsuleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(timeCapsuleContentService.getContent(timeCapsuleId, pageable));
    }

    @Operation(summary = "내 타임캡슐 내용 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}/my-contents\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/api/time-capsule-content/{timeCapsuleId}/my-contents\"}")))
    })
    @GetMapping("/{timeCapsuleId}/my-contents")
    public ResponseEntity<List<MyTimeCapsuleContentResDto>> getMyContents(
            @PathVariable Long timeCapsuleId
    ) {
        List<MyTimeCapsuleContentResDto> dto = timeCapsuleContentService.getMyContent(timeCapsuleId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "타임캡슐 내용 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공"),
            @ApiResponse(responseCode = "400", description = "파일 ID 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "파일 ID 필요", value = "{\"status\": \"400\", \"error\": \"FILE_IDS_REQUIRED\", \"message\": \"삭제할 파일 ID를 입력하세요.\", \"path\": \"/api/time-capsule-content/{contentId}\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/api/time-capsule-content/{contentId}\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/api/time-capsule-content/{contentId}\"}"))),
            @ApiResponse(responseCode = "404", description = "타임캡슐 내용을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "타임캡슐 내용을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"CONTENT_NOT_FOUND\", \"message\": \"타임캡슐을 내용을 찾을 수 없습니다.\", \"path\": \"/api/time-capsule-content/{contentId}\"}")))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteContent(
            @RequestParam List<Long> contentIds,
            @RequestParam(required = false) List<Long> fileIds) {
        timeCapsuleContentService.deleteContent(contentIds, fileIds);
        return ResponseEntity.ok().build();
    }
}
