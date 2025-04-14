package com.mysite.stockburning.authentication.authenticationProvider;

import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.authentication.CustomUserDetailsService;
import com.mysite.stockburning.authentication.PasswordEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncryptor passwordEncryptor;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("[DaoAuthenticationProvider] - AuthenticationManager 로 부터 객체 반환받음");
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(username);

        if(!passwordEncryptor.matches(password, customUserDetails.getPassword())){
            throw new BadCredentialsException("유효하지 않는 비밀번호");
        }
        log.info("[DaoAuthenticationProvider] - UsernamePasswordAuthenticationToken 객체 리턴");
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.info("[AuthenticationManager] - DaoAuthenticationProvider 발견");
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
