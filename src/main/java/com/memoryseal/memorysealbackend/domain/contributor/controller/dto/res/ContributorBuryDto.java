package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContributorBuryDto {
    private ContributorRole contributorRole;

    private Boolean bury;

    private Long userId;
}
