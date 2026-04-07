package com.memoryseal.memorysealbackend.domain.invite.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequest;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@Schema(
        description = "타임캡슐 공동작업자 요청시 응답 DTO",
        requiredProperties = {"requestId", "status", "userId", "capsuleId"}
)
public class InviteSubmitResDto {
    private Long requestId;
    private ContributorRequestStatus status;
    private Long userId;
    private Long capsuleId;

    public static InviteSubmitResDto toDto(ContributorRequest request) {
        if(request == null) {
            return null;
        }
        return InviteSubmitResDto.builder()
                .requestId(request.getId())
                .status(request.getStatus())
                .userId(request.getUserId())
                .capsuleId(request.getTimeCapsuleId())
                .build();
    }
}
