package com.memoryseal.memorysealbackend.global.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "에러 케이스")
public class ErrorResponse {
    @Schema(description = "에러 발생 시간", example = "2026-01-29T15:40:12.123")
    private final LocalDateTime timestamp;

    @Schema(description = "HTTP 상태 코드", example = "401")
    private final int status;

    @Schema(description = "에러 유형"/*, example = "INVALID_PARAMETER"*/)
    private final String error;

    @Schema(description = "에러 상세 메세지"/*, example = "만료된 토큰입니다."*/)
    private final String message;

    @Schema(description = "API 호출 경로"/*, example = "/auth/reissue"*/)
    private final String path;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, String path) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(errorCode.getStatus().value())
                        .error(errorCode.name())
                        .message(errorCode.getMessage())
                        .path(path)
                        .build()
                );
    }
}
