package com.memoryseal.memorysealbackend.domain.user.controller.dto.req;

import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@Schema(
        description = "유저 등록 요청 DTO",
        requiredProperties = {"nickname"}
)
public class UserCreateDto {

    private String nickname;

}
