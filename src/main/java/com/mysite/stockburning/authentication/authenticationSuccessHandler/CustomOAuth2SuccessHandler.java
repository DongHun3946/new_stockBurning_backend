
package com.mysite.stockburning.authentication.authenticationSuccessHandler;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.dto.response.LoginResponse;
import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.service.UserService;
import com.mysite.stockburning.util.ProviderType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//로그인 성공 시 jwt 토큰 발급
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("[CustomOAuth2SuccessHandler] - Authentication 객체 얻어옴");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long providerId = oAuth2User.getProviderId();
        ProviderType providerType = oAuth2User.getProviderType();
        LoginResponse loginResponse = userService.loginAsOauth2(providerType, providerId);
        TokenResponse tokenResponse = jwtUtil.getToken(loginResponse.id(), loginResponse.nickName(), loginResponse.role(), providerType);

        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        setRefreshTokenCookie(response, tokenResponse.refreshToken());

        response.sendRedirect(baseUrl);
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
