package com.memoryseal.memorysealbackend.domain.invite.controller;

import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.invite.service.InviteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

    //@PostMapping("/")
}
