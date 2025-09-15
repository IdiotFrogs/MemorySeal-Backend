package com.memoryseal.memorysealbackend.domain.time_capsule.controller;

import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleContentRequest;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import com.memoryseal.memorysealbackend.domain.time_capsule.service.TimeCapsuleContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/time-capsule-content")
@RequiredArgsConstructor
public class TimeCapsuleContentController {
    private final TimeCapsuleContentService timeCapsuleContentService;

    @PostMapping("/{timeCapsuleId")
    public ResponseEntity<TimeCapsuleContent> createContent(
            @PathVariable Long timeCapsuleId,
            @ModelAttribute TimeCapsuleContentRequest request) {
        try {
            TimeCapsuleContent newContent = timeCapsuleContentService.createContent(timeCapsuleId, request);
            return new ResponseEntity<>(newContent, HttpStatus.CREATED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
