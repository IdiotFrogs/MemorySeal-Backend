package com.memoryseal.memorysealbackend.domain.invite.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@Schema(
        description = "타임캡슐 초대 코드 생성 성공시 응답 DTO",
        requiredProperties = {"code"}
)
public class InviteResponseDto {
    private String code;
}
