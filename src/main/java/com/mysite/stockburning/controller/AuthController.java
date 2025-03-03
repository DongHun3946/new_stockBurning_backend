package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.authentication.JwtProvider;
import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.getRefreshTokenFromCookie(request);
        Map<Object, Object> userInfo = null;

        if (refreshToken != null) {
            try{
                if (jwtProvider.isValidRefreshToken(refreshToken)) {
                    userInfo = jwtProvider.reissueAllTokens(response, refreshToken);
                } else {
                    userInfo = jwtProvider.reissueAccessToken(response, refreshToken);
                }
            }catch(Exception e){
                log.error("/api/auth/refresh 처리 도중 에러 발생 : ", e);
            }
            assert userInfo != null;
            Long id = (Long) userInfo.get("id");
            String nickName = String.valueOf(userInfo.get("userNickName"));
            String userId = String.valueOf(userInfo.get("userId"));
            String role = String.valueOf(userInfo.get("userRole"));
            String userImage = String.valueOf(userInfo.get("userImage"));
            UserInfoDTO userInfoDTO = new UserInfoDTO(id, nickName, userId, userImage, role);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userInfoDTO);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인을 시도해주세요.");
        }
    }
}

/*
refreshToken 이 없는 경우
refreshToken 이 있고 유효한 경우 accessToken 만 재발급
refreshToken 이 있고 유효하지 않은 경우 accessToken 과 refreshToken 모두 재발급
 */