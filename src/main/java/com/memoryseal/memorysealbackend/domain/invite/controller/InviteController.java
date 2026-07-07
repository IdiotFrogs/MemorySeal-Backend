package com.memoryseal.memorysealbackend.domain.invite.controller;

import com.memoryseal.memorysealbackend.domain.invite.controller.dto.req.InviteRequestDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.invite.service.InviteService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invite")
public class InviteController {
    private final InviteService inviteService;
    private final TimeCapsuleService timeCapsuleService;

    @Operation(summary = "타임캡슐 초대 코드 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/invite\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 묻힌 타임캡슐임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"409\", \"error\": \"ALREADY_BURIED\", \"message\": \"이미 묻힌 타임캡슐입니다.\", \"path\": \"/time-capsules/{capsuleId}/invite\"}")))
    })
    @PostMapping("/time-capsules/{capsuleId}/invite")
    public ResponseEntity<InviteResponseDto> generateInviteCode(
            @Parameter(description = "타임캡슐 ID")
            @PathVariable final Long capsuleId) {
        final InviteResponseDto inviteResponseDto = inviteService.generateInviteCode(capsuleId);
        return ResponseEntity.ok(inviteResponseDto);
    }

    @Operation(
            summary = "공동작업자 요청",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "초대 코드를 담은 DTO",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InviteRequestDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않거나 만료된 초대 코드",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"400\", \"error\": \"INVALID_INVITE_CODE\", \"message\": \"유효하지 않거나 만료된 초대 코드입니다.\", \"path\": \"/time-capsules/join-request\"}"))),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/join-request\"}"))),
            @ApiResponse(responseCode = "409", description = "1/ 이미 보내진 요청 \t\n 2. 이미 등록 완료된 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "이미 등록 완료된 사용자", value = "{\"status\": \"409\", \"error\": \"ALREADY_CONTRIBUTOR\", \"message\": \"이미 공동작업자로 등록이 완료된 사용자입니다.\", \"path\": \"/time-capsules/join-request\"}")))
    })
    @PostMapping("/time-capsules/join-request")
    public ResponseEntity<Void> submitContributorRequest(
            @RequestBody final InviteRequestDto requestDto) {
        inviteService.submitContributorRequest(requestDto.getCode());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 링크를 통한 타임캡슐 참가")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공"),
            @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/{capsuleId}/join\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 등록 완료된 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"409\", \"error\": \"ALREADY_CONTRIBUTOR\", \"message\": \"이미 공동작업자로 등록이 완료된 사용자입니다.\", \"path\": \"/time-capsules/{capsuleId}/join\"}")))
    })
    @PostMapping("/time-capsules/{capsuleId}/join")
    public ResponseEntity<TimeCapsuleResponseDto> joinCapsule(
            @PathVariable Long capsuleId
    ) {
        inviteService.joinCapsule(capsuleId);
        return ResponseEntity.ok(timeCapsuleService.getDetail(capsuleId));
    }

}
