package com.memoryseal.memorysealbackend.domain.invite.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProcessRequestDto(
        @Schema(description = "참여 요청 승인 여부")
        boolean isApproved
) {
}
