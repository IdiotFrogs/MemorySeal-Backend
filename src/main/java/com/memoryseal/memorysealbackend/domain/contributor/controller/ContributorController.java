package com.memoryseal.memorysealbackend.domain.contributor.controller;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.service.ContributorService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
@Tag(name = "Contributor")
public class ContributorController {

    private final ContributorService contributorService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/time-capsules/{capsuleId}/collaborators\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/collaborators\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 사용자를 찾을 수 없음 \t\n 2. 타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/time-capsules/{capsuleId}/collaborators\"}"),
                            @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/collaborators\"}")
                    }))
    })
    @GetMapping("/{capsuleId}/collaborators")
    @Operation(summary = "공동 작업자 리스트 조회")
    public List<ContributorResponseDto> getDetail(
            @Parameter(description = "타임캡슐 ID", required = true)
            @PathVariable Long capsuleId) {
        return contributorService.getDetail(capsuleId);
    }
}
