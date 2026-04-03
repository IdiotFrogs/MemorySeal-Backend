package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BuryResponseDto {
    private Long timeCapsuleId;
    private TimeCapsuleStatus status;
    private List<ContributorBuryDto> contributors;
}
