package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TimeCapsuleNameDto {

    private Long timeCapsuleId;

    private String title;

    private ContributorRole role;
}
