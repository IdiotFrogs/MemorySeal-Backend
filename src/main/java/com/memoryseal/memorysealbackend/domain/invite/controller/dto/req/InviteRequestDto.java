package com.memoryseal.memorysealbackend.domain.invite.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        description = "공동작업자 요청시 사용되는 요청 DTO",
        requiredProperties = {"code"}
)
public class InviteRequestDto {
    private String code;
}
