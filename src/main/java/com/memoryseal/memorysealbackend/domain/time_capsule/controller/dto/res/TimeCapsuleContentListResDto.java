package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TimeCapsuleContentListResDto {
    private ContributorRole myRole;
    private List<TimeCapsuleContentResDto> contents;
}
