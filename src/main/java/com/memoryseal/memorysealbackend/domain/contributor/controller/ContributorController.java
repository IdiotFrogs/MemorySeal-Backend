package com.memoryseal.memorysealbackend.domain.contributor.controller;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.service.ContributorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/time-capsules")
@Tag(name = "Contributor")
public class ContributorController {

    private final ContributorService contributorService;

    @GetMapping("/{capsuleId}/collaborators")
    public List<ContributorResponseDto> getDetail(@PathVariable Long capsuleId) {
        return contributorService.getDetail(capsuleId);
    }
}
