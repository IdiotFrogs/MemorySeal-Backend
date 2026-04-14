package com.memoryseal.memorysealbackend.domain.invite.controller;

import com.memoryseal.memorysealbackend.domain.invite.controller.dto.req.InviteRequestDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.req.ProcessRequestDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.ContributorRequestResDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteSubmitResDto;
import com.memoryseal.memorysealbackend.domain.invite.service.InviteService;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.ErrorResponse;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invite")
public class InviteController {
    private final InviteService inviteService;

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
                    examples = {
                            @ExampleObject(name = "이미 보내진 요청", value = "{\"status\": \"409\", \"error\": \"ALREADY_REQUESTED\", \"message\": \"이미 공동작업자 요청을 보냈습니다.\", \"path\": \"/time-capsules/join-request\"}"),
                            @ExampleObject(name = "이미 등록 완료된 사용자", value = "{\"status\": \"409\", \"error\": \"ALREADY_CONTRIBUTOR\", \"message\": \"이미 공동작업자로 등록이 완료된 사용자입니다.\", \"path\": \"/time-capsules/join-request\"}"),
                    }))
    })
    @PostMapping("/time-capsules/join-request")
    public ResponseEntity<InviteSubmitResDto> submitContributorRequest(
            @RequestBody final InviteRequestDto requestDto) {
        InviteSubmitResDto response = inviteService.submitContributorRequest(requestDto.getCode());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "공동작업자 요청 승인",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "승인/거절 여부를 담은 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProcessRequestDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "요청을 처리할 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/request/{capsuleId}/{requestId}/process\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 타임캡슐을 찾을 수 없음 \t\n 2. 공동작업자 요청을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "타임캡슐을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/request/{capsuleId}/{requestId}/process\"}"),
                            @ExampleObject(name = "공동작업자 요청을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"REQUEST_NOT_FOUND\", \"message\": \"공동작업자 요청을 찾을 수 없습니다.\", \"path\": \"/time-capsules/request/{capsuleId}/{requestId}/process\"}")
                    })),
            @ApiResponse(responseCode = "409", description = "이미 처리된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "이미 처리된 요청", value = "{\"status\": \"409\", \"error\": \"ALREADY_PROCESSED\", \"message\": \"이미 처리된 요청입니다.\", \"path\": \"/time-capsules/request/{capsuleId}/{requestId}/process\"}")))
    })
    @PostMapping("/time-capsules/request/{requestId}/process")
    public ResponseEntity<InviteSubmitResDto> processContributorRequest(
            @Parameter(description = "처리할 요청의 ID", required = true)
            @PathVariable final Long requestId,
            @RequestBody final ProcessRequestDto requestDto) {
        InviteSubmitResDto response = inviteService.processContributorRequest(requestId, requestDto.isApproved());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공동작업자 요청 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "403", description = "요청을 처리할 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"403\", \"error\": \"ACCESS_DENIED\", \"message\": \"해당 요청을 처리할 권한이 없습니다.\", \"path\": \"/time-capsules/request/{capsuleId}/requests\"}"))),
            @ApiResponse(responseCode = "404", description = "1. 타임캡슐을 찾을 수 없음 \t\n 2. 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "타임캡슐을 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"TIMECAPSULE_NOT_FOUND\", \"message\": \"타임캡슐을 찾을 수 없습니다.\", \"path\": \"/time-capsules/request/{capsuleId}/requests\"}"),
                            @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/time-capsules/request/{capsuleId}/requests\"}")
                    })),
    })
    @GetMapping("/time-capsules/request/{capsuleId}/requests")
    public ResponseEntity<List<ContributorRequestResDto>> getReqeustsDetail(@PathVariable Long capsuleId) {
        List<ContributorRequestResDto> response = inviteService.getReqeustsDetail(capsuleId);
        return ResponseEntity.ok(response);
    }

}
