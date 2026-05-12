package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserContentDto {
    @Schema(description = "유저 ID")
    private Long userId;
    @Schema(description = "유저 닉네임")
    private String nickname;
    @Schema(description = "타임캡슐 내용")
    private List<TimeCapsuleContentResDto> capsuleContents;
}
