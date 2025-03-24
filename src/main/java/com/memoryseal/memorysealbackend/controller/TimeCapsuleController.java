package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.dto.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.dto.UserCreateDto;
import com.memoryseal.memorysealbackend.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.entity.User;
import com.memoryseal.memorysealbackend.enums.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.service.TimeCapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @PostMapping("/create")
    public void createTimeCapsule(@RequestBody TimeCapsuleCreateDto timeCapsuleCreateDto) {
        TimeCapsule timeCapsule = timeCapsuleService.createTimeCapsule(timeCapsuleCreateDto);
    }
}
