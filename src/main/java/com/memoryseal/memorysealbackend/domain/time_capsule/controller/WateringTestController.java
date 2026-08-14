package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.WateringResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleWatering;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.WateringJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.service.WateringService;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("dev/water")
@RequiredArgsConstructor
@Slf4j
public class WateringTestController {

    private final WateringJpaRepository wateringJpaRepository;
    private final WateringService wateringService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }
        Object principal = authentication.getPrincipal();
        Long currentUserId;
        if(principal instanceof User) {
            currentUserId = ((User) principal).getId();
        }else if(principal instanceof String) {
            currentUserId = Long.valueOf((String) principal);
        }else {
            log.error("예상치 못한 Principal 타입: {}", principal.getClass().getName());
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }
        return currentUserId;
    }
    @PostMapping("/{capsuleId}/bulk")
    public ResponseEntity<Void> createBulkWatering(@PathVariable Long capsuleId, @RequestParam int count) {
        List<TimeCapsuleWatering> waterings = new ArrayList<>();
        Long currentUserId = getCurrentUserId();
        for(int i = 0; i < count; i++) {
            waterings.add(TimeCapsuleWatering.builder()
                    .timeCapsuleId(capsuleId)
                    .userId(currentUserId)
                    .wateredDate(LocalDate.now().plusDays(i))
                    .build());
        }
        wateringJpaRepository.saveAll(waterings);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{capsuleId}")
    public ResponseEntity<WateringResponseDto> getAllWatering(
            @PathVariable Long capsuleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(wateringService.getAllWatering(capsuleId, pageable));
    }

}
