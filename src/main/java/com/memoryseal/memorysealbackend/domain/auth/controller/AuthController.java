package com.memoryseal.memorysealbackend.domain.auth.controller;

import com.memoryseal.memorysealbackend.domain.auth.controller.dto.req.AppleLoginRequest;
import com.memoryseal.memorysealbackend.domain.auth.controller.dto.req.GoogleLoginRequest;
import com.memoryseal.memorysealbackend.domain.auth.service.RefreshTokenService;
import com.memoryseal.memorysealbackend.global.error.ErrorResponse;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.domain.auth.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    /*
    @Operation(summary = "Google 로그인")
    @GetMapping("/login/google")
    public void loginGoogle(HttpServletResponse response) throws IOException {
        String googleLoginUrl = "/oauth2/authorization/google";
        response.sendRedirect(googleLoginUrl);
    }
    */

    @Operation(summary = "Access Token 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "1. 토큰 만료 \t\n 2. 유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "토큰 만료", value = "{\"status\": \"401\", \"error\": \"EXPIRED_TOKEN\", \"message\": \"만료된 토큰입니다.\", \"path\": \"/auth/reissue\"}"),
                            @ExampleObject(name = "유효하지 않은 토큰", value = "{\"status\": \"401\", \"error\": \"INVALID_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"path\": \"/auth/reissue\"}")
                    })),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/auth/reissue\"}")))
    })
    @PostMapping("/reissue")
    public ResponseEntity<GeneratedToken> reissueAccessToken(
            @Parameter(description = "Refresh Token", required = true)
            @RequestHeader("RefreshToken") String refreshToKen) {
        try{
            GeneratedToken newToken = refreshTokenService.reissue(refreshToKen);
            return ResponseEntity.ok(newToken);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
    @GetMapping("/login/success")
    public String testLoginSuccess(
            @RequestParam(value = "accessToken", required = false) String accessToken,
            @RequestParam(value = "refreshToken", required = false) String refreshToken
    ) {
        return "로그인 성공. <br><br>" +
                "AccessToken: " + accessToken + "<br>" +
                "RefreshToken: " + refreshToken;
    }

     */

    @Operation(summary = "Google 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"400\", \"error\": \"INVALID_PARAMETER\", \"message\": \"파라미터 값을 확인해 주세요\", \"path\": \"/auth/login/google\"}"))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"401\", \"error\": \"INVALID_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"path\": \"/auth/login/google\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"404\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/auth/login/google\"}")))
    })
    @PostMapping("/login/google")
    public ResponseEntity<GeneratedToken> loginGoogle(
            @RequestBody GoogleLoginRequest request
            ) {
        String providerId = loginService.verifyGoogleIdToken(request.getIdToken());
        GeneratedToken response = loginService.execute(providerId, "google");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Apple 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"401\", \"error\": \"INVALID_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"path\": \"/auth/login/apple\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"status\": \"401\", \"error\": \"USER_NOT_FOUND\", \"message\": \"존재하지 않는 사용자 입니다.\", \"path\": \"/auth/login/apple\"}")))
    })
    @PostMapping("/login/apple")
    public ResponseEntity<GeneratedToken> loginApple(
            @RequestBody AppleLoginRequest request
    ) {
        String providerId = loginService.verifyAppleIdToken(request.getIdToken(), request.getAuthorizationCode());
        GeneratedToken response = loginService.execute(providerId, "apple");
        return ResponseEntity.ok(response);
    }

    /*
    @Operation(summary = "로그아웃")
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "현재 유효한 Access Token", required = true)
            @RequestHeader("Authorization") String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authorizationHeader.substring(7);

        try {
            refreshTokenService.removeRefreshToken(accessToken);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
     */
}
