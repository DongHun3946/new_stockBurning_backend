package com.mysite.stockburning.configuration;

import com.mysite.stockburning.authentication.authenticationFilter.JwtAuthenticationFilter;
import com.mysite.stockburning.authentication.CustomOAuth2SuccessHandler;
import com.mysite.stockburning.authentication.CustomOAuth2UserService;
import com.mysite.stockburning.authentication.authenticationProvider.DaoAuthenticationProvider;
import com.mysite.stockburning.authentication.authenticationProvider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //@PreAuthorize 사용하기 위함
@RequiredArgsConstructor
public class SecurityConfig{
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return new ProviderManager(List.of(
                daoAuthenticationProvider,
                jwtAuthenticationProvider)
        );
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf((auth) -> auth.disable())
                .formLogin(form -> form.disable());


        http
                .oauth2Login((oauth2) -> oauth2
                        .redirectionEndpoint((endpoint) -> endpoint.baseUri("/oauth/kakao/authorization")) //인가코드를 반환하는 리다이렉션 URL
                        .userInfoEndpoint((endpoint) -> endpoint.userService(customOAuth2UserService)) //로그인 후 카카오는 액세스 토큰을 반환하고 이를 사용하여 사용자 정보를 가져오는 과정을 CustomOAuth2UserService 에서 처리
                        .successHandler(customOAuth2SuccessHandler));


        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //cors 용
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated());


        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization", HttpHeaders.LOCATION));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}