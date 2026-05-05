package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserContentDto {
    private Long userId;
    private String nickname;
    private List<TimeCapsuleContentResDto> contents;
}
