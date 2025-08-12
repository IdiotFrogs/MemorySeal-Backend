package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleUpdateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleNameDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.service.TimeCapsuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
@Tag(name = "Time Capsule")
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @PostMapping("/create")
    public ResponseEntity<TimeCapsule> createTimeCapsule(@RequestBody TimeCapsuleCreateDto timeCapsuleCreateDto) {
        TimeCapsule timeCapsule = timeCapsuleService.createTimeCapsule(timeCapsuleCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeCapsule);
    }

    @GetMapping("/{capsuleId}")
    public ResponseEntity<TimeCapsuleResponseDto> getDetail(@PathVariable Long capsuleId) {
        TimeCapsuleResponseDto dto = timeCapsuleService.getDetail(capsuleId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{capsuleId}")
    public ResponseEntity<TimeCapsuleUpdateDto> updateTimeCapsule(@PathVariable Long capsuleId, @RequestBody TimeCapsuleUpdateDto timeCapsuleUpdateDto) {
        TimeCapsuleUpdateDto dto = timeCapsuleService.updateTimeCapsule(capsuleId, timeCapsuleUpdateDto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my")
    public  ResponseEntity<List<TimeCapsuleNameDto>> getMyTimeCapsule() {
        List<TimeCapsuleNameDto> myTimeCapsule = timeCapsuleService.getTimeCapsule();
        return ResponseEntity.ok(myTimeCapsule);
    }
}
