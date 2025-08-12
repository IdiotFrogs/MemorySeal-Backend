package com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributorResponseDto {

    private ContributorRole contributorRole;

    private Boolean bury;

    private Long userId;

    public static ContributorResponseDto toDto(Contributor contributor) {
        if (contributor == null) {
            return null;
        }else {
            return ContributorResponseDto.builder()
                    .contributorRole(contributor.getContributorRole())
                    .bury(contributor.getBury())
                    .userId(contributor.getUserId())
                    .build();
        }
    }
}
