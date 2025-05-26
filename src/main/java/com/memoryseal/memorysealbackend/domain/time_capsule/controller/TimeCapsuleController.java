package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.dto.*;
import com.memoryseal.memorysealbackend.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.entity.User;
import com.memoryseal.memorysealbackend.enums.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.service.TimeCapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @PostMapping("/create")
    public void createTimeCapsule(@RequestBody TimeCapsuleCreateDto timeCapsuleCreateDto) {
        TimeCapsule timeCapsule = timeCapsuleService.createTimeCapsule(timeCapsuleCreateDto);
    }

    @GetMapping("/{capsuleId}")
    public TimeCapsuleResponseDto getDetail(@PathVariable Long capsuleId) {
        return timeCapsuleService.getDetail(capsuleId);
    }

    @PutMapping("/{capsuleId}")
    public TimeCapsuleUpdateDto updateTimeCapsule(@PathVariable Long capsuleId, @RequestBody TimeCapsuleUpdateDto timeCapsuleUpdateDto) {
        return timeCapsuleService.updateTimeCapsule(capsuleId, timeCapsuleUpdateDto);
    }
}
