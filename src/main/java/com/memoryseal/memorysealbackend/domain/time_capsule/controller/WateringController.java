package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.WateringResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.service.WateringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/time-capsules")
@RequiredArgsConstructor
@Tag(name = "Watering")
public class WateringController {
    private final WateringService wateringService;

    @Operation(summary = "물주기")
    @PostMapping("/{capsuleId}/water")
    public ResponseEntity<Void> water(@PathVariable Long capsuleId) {
        wateringService.water(capsuleId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "물주기 현황 조회")
    @GetMapping("/{capsuleId}/water")
    public ResponseEntity<WateringResponseDto> getWatering(
            @PathVariable Long capsuleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(wateringService.getWatering(capsuleId, pageable, sort));
    }
}
