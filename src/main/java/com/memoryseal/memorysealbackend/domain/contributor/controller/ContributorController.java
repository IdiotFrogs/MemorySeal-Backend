package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.dto.ContributorResponseDto;
import com.memoryseal.memorysealbackend.dto.UserResponseDto;
import com.memoryseal.memorysealbackend.service.ContributorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
public class ContributorController {

    private final ContributorService contributorService;

    @GetMapping("/{capsuleId}/collaborators")
    public List<ContributorResponseDto> getDetail(@PathVariable Long capsuleId) {
        return contributorService.getDetail(capsuleId);
    }
}
