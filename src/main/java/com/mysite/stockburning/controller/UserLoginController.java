package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.JwtProvider;
import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.dto.request.LoginRequest;
import com.mysite.stockburning.dto.response.LoginResponse;
import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.service.UserService;
import com.mysite.stockburning.util.ProviderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "로그인") //Swagger 문서에서 이 컨트롤러를 "로그인" 섹션으로 그룹화
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserService userService;
    private final JwtProvider tokenProvider;

    @Operation(summary = "StockBurning 자체 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<UserInfoDTO> login(@Valid @RequestBody LoginRequest request,
                                        HttpServletResponse response) throws IOException {
        LoginResponse loginResponse = userService.login(request); //사용자 정보 인증확인
        TokenResponse tokenResponse = tokenProvider.getToken(loginResponse.id(), loginResponse.nickName(), loginResponse.userId(), loginResponse.role(), loginResponse.profileImageUrl(), String.valueOf(ProviderType.LOCAL));
        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        setRefreshTokenCookie(response, tokenResponse.refreshToken());

        UserInfoDTO userInfoDTO = new UserInfoDTO(loginResponse.id(), loginResponse.nickName(), loginResponse.userId(), loginResponse.profileImageUrl(), String.valueOf(loginResponse.role()));

        return ResponseEntity.ok().body(userInfoDTO);
    }
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);
            tokenProvider.reissueAllTokens(response, refreshToken);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("토큰 재발급 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("토큰 재발급 중 에러: " + e.getMessage());
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);      //js 에서 쿠키에 접근할 수 없도록 함(xss 공격 방지)
        cookie.setSecure(false);       //https 연결에서만 쿠키 전송 true -> https, false -> http
        cookie.setPath("/");           //쿠키가 적용될 경로
        cookie.setMaxAge(60*60*24*30); //쿠키 유효 기간(30일)
        cookie.setAttribute("SameSite", "Lax"); //Strict 외부사이트에서 요청할 경우 쿠키를 전송하지 않도록 설정 (CSRF 공격 방지)

        response.addCookie(cookie);
    }
}
