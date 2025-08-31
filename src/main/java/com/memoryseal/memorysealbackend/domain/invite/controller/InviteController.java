package com.memoryseal.memorysealbackend.domain.invite.controller;

import com.memoryseal.memorysealbackend.domain.invite.controller.dto.req.InviteRequestDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.req.ProcessRequestDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.invite.service.InviteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invite")
public class InviteController {
    private final InviteService inviteService;
    @PostMapping("/time-capsules/{capsuleId}/invite")
    public ResponseEntity<InviteResponseDto> generateInviteCode(@PathVariable final Long capsuleId) {
        final InviteResponseDto inviteResponseDto = inviteService.generateInviteCode(capsuleId);
        return ResponseEntity.ok(inviteResponseDto);
    }

    @PostMapping("/time-capsule/{capsuleId}/join-request")
    public ResponseEntity<Void> submitContributorRequest(@PathVariable final Long capsuleId, @RequestBody final InviteRequestDto requestDto, @AuthenticationPrincipal final UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        inviteService.submitContributorRequest(capsuleId, requestDto.getCode(), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/time-capsule/request/{requestId}/process")
    public ResponseEntity<Void> processContributorRequest(@PathVariable final Long requestId, @RequestBody final ProcessRequestDto requestDto, @AuthenticationPrincipal final UserDetails userDetails) throws AccessDeniedException {
        Long hostId = getUserIdFromUserDetails(userDetails);
        inviteService.processContributorRequest(requestId, hostId, requestDto.isApproved());
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자");
        }

        Object principal = authentication.getPrincipal();
        if(principal instanceof UserDetails) {
            return Long.valueOf(((UserDetails) principal).getUsername());
        }else {
            throw new IllegalStateException("사용자 ID를 가져올 수 없음");
        }
    }
}
