package com.memoryseal.memorysealbackend.domain.contributor.controller;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.req.BuryRequestDto;
import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.PageResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.service.ContributorService;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PageResponseDto<ContributorResponseDto>> getDetail(
            @Parameter(description = "타임캡슐 ID", required = true)
            @PathVariable Long capsuleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PageResponseDto<>(contributorService.getDetail(capsuleId, pageable)));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "openedAt이 현재보다 과거임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "openedAt이 현재보다 과거임", value = "{\"status\": \"400\", \"error\": \"INVALID_OPENED_AT\", \"message\": \"openedAt이 현재보다 과거입니다.\", \"path\": \"/time-capsules/{capsuleId}/bury/agree\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "로그인 필요", value = "{\"status\": \"401\", \"error\": \"NEED_LOGIN\", \"message\": \"로그인이 필요합니다.\", \"path\": \"/time-capsules/{capsuleId}/bury/agree\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/bury/agree\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 사용자를 찾을 수 없음 \t\n 2. 타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/time-capsules/{capsuleId}/bury/agree\"}"),
                            @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/bury/agree\"}")
                    })),
            @ApiResponse(responseCode = "409", description = "이미 묻힌 타임캡슐",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "이미 묻힌 타임캡슐", value = "{\"status\": \"409\", \"error\": \"ALREADY_BURIED\", \"message\": \"이미 묻힌 타임캡슐입니다.\", \"path\": \"/time-capsules/{capsuleId}/bury/agree\"}")))
    })
    @PutMapping("/{capsuleId}/bury")
    @Operation(summary = "타임캡슐 묻기")
    public ResponseEntity<TimeCapsuleResponseDto> agreeBury(@PathVariable Long capsuleId, @Valid @RequestBody BuryRequestDto request) {
        return ResponseEntity.ok(contributorService.buryCapsule(capsuleId, request.getOpenedAt()));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "호스트는 추방할 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "호스트는 추방할 수 없음", value = "{\"status\": \"400\", \"error\": \"CANNOT_KICK_HOST\", \"message\": \"호스트는 추방할 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/contributors/{targetUserId}\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/contributors/{targetUserId}\"}"))),
    })
    @DeleteMapping("/{capsuleId}/contributors/{targetUserId}")
    @Operation(summary = "공동작업자 추방")
    public ResponseEntity<Void> kickContributor(
            @PathVariable Long capsuleId,
            @PathVariable Long targetUserId) {
        contributorService.kickContributor(capsuleId, targetUserId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "호스트는 타임캡슐을 나갈 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "호스트는 타임캡슐을 나갈 수 없음", value = "{\"status\": \"400\", \"error\": \"HOST_CANNOT_LEAVE\", \"message\": \"호스트는 타임캡슐을 나갈 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/leave\"}"))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/leave\"}"))),
    })
    @DeleteMapping("/{capsuleId}/leave")
    @Operation(summary = "타임캡슐 나가기")
    public ResponseEntity<Void> leaveTimeCapsule(
            @PathVariable Long capsuleId
    ) {
        contributorService.leaveTimeCapsule(capsuleId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "자기 자신에게 위임 불가",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "자기 자신에게 위임 불가", value = "{\"status\": \"400\", \"error\": \"CANNOT_DELEGATE_TO_SELF\", \"message\": \"자기 자신에게 호스트를 위임할 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/leave\"}"))),
            @ApiResponse(responseCode = "403", description = "1. 접근 권한 없음 \t\n 2. 공동작업자 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다\", \"path\": \"/time-capsules/{capsuleId}/leave\"}"),
                            @ExampleObject(name = "공동작업자 아님", value = "{\"status\": \"403\", \"error\": \"NOT_A_CONTRIBUTOR\", \"message\": \"해당 타임캡슐의 공동작업자가 아닙니다.\", \"path\": \"/time-capsules/{capsuleId}/leave\"}")
                    }))
    })
    @PutMapping("/{capsuleId}/delegation/{targetUserId}")
    @Operation(summary = "호스트 위임")
    public ResponseEntity<Void> delegationHost(
            @PathVariable Long capsuleId,
            @PathVariable Long targetUserId
    ) {
        contributorService.delegationHost(capsuleId, targetUserId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "접근 권한 없음", value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다\", \"path\": \"/time-capsules/{capsuleId}/collaborators/search\"}"))),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "타임캡슐을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/collaborators/search\"}")))
    })
    @GetMapping("/{capsuleId}/collaborators/search")
    @Operation(summary = "공동 작업자 닉네임 검색")
    public ResponseEntity<PageResponseDto<ContributorResponseDto>> searchByNickname(
            @PathVariable Long capsuleId,
            @RequestParam(required = false) String nickname,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PageResponseDto<>(contributorService.searchByNickname(capsuleId, nickname, pageable)));
    }
}
