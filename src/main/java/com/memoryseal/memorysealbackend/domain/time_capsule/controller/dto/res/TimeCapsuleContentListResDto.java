package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "타임캡슐 내용 목록 조회 DTO", requiredProperties = {"myRole", "contents"})
public class TimeCapsuleContentListResDto {
    @Schema(description = "조회한 유저의 역할", examples = {"HOST", "CONTRIBUTOR"})
    private ContributorRole myRole;

    @Schema(description = "타임캡슐 내용 DTO")
    private List<UserContentDto> userContents;
}
