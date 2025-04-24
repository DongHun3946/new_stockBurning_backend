package com.mysite.stockburning.authentication.authenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.stockburning.authentication.authenticationFailureHandler.CustomAuthenticationFailureHandler;
import com.mysite.stockburning.authentication.authenticationSuccessHandler.CustomAuthenticationSuccessHandler;
import com.mysite.stockburning.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                      CustomAuthenticationSuccessHandler successHandler,
                                                      CustomAuthenticationFailureHandler failureHandler){
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/login");
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST") || !request.getContentType().contains("application/json")) {
            throw new AuthenticationServiceException("지원하지 않는 로그인 방식입니다.");
        }
        log.info("[UsernamePasswordAuthenticationFilter]");
        try (InputStream inputStream = request.getInputStream()) {
            // JSON 요청 바디 파싱
            LoginRequest loginRequest = objectMapper.readValue(inputStream, LoginRequest.class);

            String username = loginRequest.userId();
            String password = loginRequest.userPw();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("유효하지 않는 로그인 요청");
        }
    }
}
