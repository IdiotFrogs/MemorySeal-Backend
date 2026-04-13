package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuryCheckResDto {
    private Long memberCount;
    private Long trueCount;
}
