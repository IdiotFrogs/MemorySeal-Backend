package com.memoryseal.memorysealbackend.global.error.Exception;

import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException{
    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
