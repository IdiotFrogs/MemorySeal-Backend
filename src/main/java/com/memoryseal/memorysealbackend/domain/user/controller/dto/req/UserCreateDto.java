package com.memoryseal.memorysealbackend.domain.user.controller.dto.req;

import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserCreateDto {

    private Long id;

    private String nickname;

    private String profileUrl;

    private String email;

    private Boolean userActiveStatus;

    private Role role;
}
