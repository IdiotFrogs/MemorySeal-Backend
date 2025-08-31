package com.memoryseal.memorysealbackend.domain.invite.controller.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteRequestDto {
    private String code;
}
