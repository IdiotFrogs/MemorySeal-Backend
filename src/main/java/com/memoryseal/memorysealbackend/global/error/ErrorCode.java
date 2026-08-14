package com.memoryseal.memorysealbackend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값을 확인해 주세요."),
    MISSING_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 전달되지 않았습니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "유효하지 않거나 만료된 초대 코드입니다."),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "내용 또는 파일 중 적어도 하나는 포함되어야 합니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다."),
    NOT_SUPPORT_LOGIN(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 방식입니다."),
    INVALID_OPENED_AT(HttpStatus.BAD_REQUEST, "openedAt이 현재보다 과거입니다."),
    CANNOT_KICK_HOST(HttpStatus.BAD_REQUEST, "호스트는 추방할 수 없습니다."),
    HOST_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "호스트는 타임캡슐을 나갈 수 없습니다."),
    CANNOT_DELEGATE_TO_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게 호스트를 위임할 수 없습니다."),
    FILE_IDS_REQUIRED(HttpStatus.BAD_REQUEST, "삭제할 파일 ID를 입력하세요."),
    INVALID_FCM_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 FCM 토큰입니다."),
    NOT_TIMECAPSULE_BURIED(HttpStatus.BAD_REQUEST, "묻힌 상태의 타임캡슐이 아닙니다."),
    INVALID_SORT_DIRECTION(HttpStatus.BAD_REQUEST, "정렬 방향은 asc 또는 desc만 가능합니다."),

    // 401 UnAuthorized
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    NEED_LOGIN(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // 403 FORBIDDEN
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 요청을 처리할 권한이 없습니다."),
    NOT_A_CONTRIBUTOR(HttpStatus.FORBIDDEN, "해당 타임캡슐의 공동작업자가 아닙니다."),

    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 입니다."),
    REFRESHTOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 리프레시 토큰이 없습니다."),
    TIMECAPSULE_NOT_FOUND(HttpStatus.NOT_FOUND, "타임캡슐을 찾을 수 없습니다."),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "공동작업자 요청을 찾을 수 없습니다."),
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "타임캡슐 내용을 찾을 수 없습니다."),

    // 409 CONFLICT(중복된 상태)
    ALREADY_CONTRIBUTOR(HttpStatus.CONFLICT, "이미 공동작업자로 등록이 완료된 사용자입니다."),
    ALREADY_ONBOARDED(HttpStatus.CONFLICT, "이미 온보딩이 완료된 유저입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    ALREADY_BURIED(HttpStatus.CONFLICT, "이미 묻힌 타임캡슐입니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제되었거나 변경된 데이터입니다."),
    ALREADY_WATERED(HttpStatus.CONFLICT, "오늘 이미 물을 줬습니다."),

    //413 PAYLOAD TOO LARGE
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 제한을 초과했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    APPLE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 서버 통신 중 오류가 발생했습니다."),
    GOOGLE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Google 서버 통신 중 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
