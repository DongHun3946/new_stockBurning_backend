package com.mysite.stockburning.authentication.authenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // JWT 토큰 생성
        TokenResponse tokenResponse = jwtUtil.getToken(userDetails.getId(), userDetails.getNickName(),
                userDetails.getUserRole(), userDetails.getProviderType());

        // 토큰을 응답 헤더에 설정
        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        jwtUtil.setRefreshToken(response, tokenResponse.refreshToken());

        // 사용자 추가 정보 조회
        String userId = userService.getUserId(userDetails.getId());
        String profilePicture = userService.getProfilePicture(userDetails.getId());

        // 사용자 정보 DTO 생성
        UserInfoDTO userInfoDTO = new UserInfoDTO(userDetails.getId(), userDetails.getNickName(),
                userId, profilePicture, userDetails.getUserRole(),
                userDetails.getProviderType());

        // 사용자 정보와 함께 응답 반환
        response.setContentType("application/json");

        //ObjectMapper : Java 객체를 JSON 형식으로 변환가능 또는 JSON 형식을 JAVA 객체로 변환가능(writeValueAsString : 자바 객체를 JSON 형식의 문자열로 변환)
        response.getWriter().write(new ObjectMapper().writeValueAsString(userInfoDTO));

        log.info("[AuthenticationSuccessHandler] - 로그인 성공, 사용자 정보 반환");
    }
}
