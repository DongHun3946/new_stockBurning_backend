package com.mysite.stockburning.authentication.authenticationProvider;

import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.authentication.CustomUserDetailsService;
import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.authentication.authenticationToken.JwtAuthenticationToken;
import com.mysite.stockburning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("[JwtAuthenticationProvider] - AuthenticationManager 로 부터 객체 반환받음");
        String accessToken = (String) authentication.getCredentials();
        if(!jwtUtil.isValidAccessToken(accessToken)){
            throw new BadCredentialsException("유효하지 않은 토큰");
        }
        Map<Object, Object> userInfo = jwtUtil.decode(true, accessToken);
        String userId = userService.getUserId((Long)userInfo.get("id"));

        CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userId);

        log.info("[JwtAuthenticationProvider] - JwtAuthenticationToken 객체 리턴");
        return new JwtAuthenticationToken(customUserDetails, accessToken, customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.info("[AuthenticationManager] - JwtAuthenticationProvider 발견");
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
