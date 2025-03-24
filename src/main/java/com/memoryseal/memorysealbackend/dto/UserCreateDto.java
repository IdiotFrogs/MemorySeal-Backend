package com.memoryseal.memorysealbackend.dto;

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
}
