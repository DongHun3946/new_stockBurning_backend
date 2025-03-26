package com.mysite.stockburning.configuration;

import com.mysite.stockburning.authentication.authenticationFilter.JwtAuthenticationFilter;
import com.mysite.stockburning.authentication.CustomOAuth2SuccessHandler;
import com.mysite.stockburning.authentication.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable());


        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .redirectionEndpoint((endpoint) -> endpoint.baseUri("/oauth/kakao/authorization")) //인가코드를 반환하는 리다이렉션 URL
                        .userInfoEndpoint((endpoint) -> endpoint.userService(customOAuth2UserService)) //로그인 후 카카오는 액세스 토큰을 반환하고 이를 사용하여 사용자 정보를 가져오는 과정을 CustomOAuth2UserService 에서 처리
                        .successHandler(customOAuth2SuccessHandler));


        http
                .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll() //OPTION : 다른 도메인으로 요청할 때 이 요청을 허용할지 서버에 확인하기 위한 요청
                                .requestMatchers("/api/**").permitAll()
                                .anyRequest().authenticated());


        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
