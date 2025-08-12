package com.memoryseal.memorysealbackend.domain.invite.controller.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class InviteResponseDto {
    private String code;
}
