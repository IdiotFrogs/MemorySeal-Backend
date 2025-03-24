package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.dto.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.dto.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.dto.UserCreateDto;
import com.memoryseal.memorysealbackend.dto.UserResponseDto;
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

    @GetMapping("/{id}")
    public TimeCapsuleResponseDto getDetail(@PathVariable Long id) {
        return timeCapsuleService.getDetail(id);
    }
}
