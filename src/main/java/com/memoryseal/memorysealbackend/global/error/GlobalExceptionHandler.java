package com.memoryseal.memorysealbackend.global.error;

import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ErrorResponse> handleAuthException(AuthException e, HttpServletRequest request) {
        log.error("AuthException: {}", e.getErrorCode(), request.getRequestURI());
        return ErrorResponse.toResponseEntity(e.getErrorCode(), request.getRequestURI());
    }

    @ExceptionHandler({MultipartException.class, MaxUploadSizeExceededException.class})
    protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(Exception e, HttpServletRequest request) {
        log.error("FILE SIZE EXCEEDED: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.FILE_SIZE_EXCEEDED, request.getRequestURI());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    protected ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException e, HttpServletRequest request
    ) {
        log.error("OPTIMISTIC LOCK CONFLICT: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.ALREADY_DELETED, request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMehtodArgumentNoValidException(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        log.error("VALIDATION ERROR: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_PARAMETER, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("INTERNAL SERVER ERROR: {}", e.getMessage(), e);
        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }
}
