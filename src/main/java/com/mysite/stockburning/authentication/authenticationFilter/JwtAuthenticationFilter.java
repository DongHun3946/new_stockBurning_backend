package com.mysite.stockburning.authentication.authenticationFilter;


import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.authentication.authenticationProvider.JwtAuthenticationProvider;
import com.mysite.stockburning.authentication.authenticationToken.JwtAuthenticationToken;
import com.mysite.stockburning.dto.request.HttpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final List<HttpRequest> whiteList;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtAuthenticationProvider jwtAuthenticationProvider){
        this.jwtUtil = jwtUtil;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;

        this.whiteList = List.of( //JWT 필터를 적용하지 않고 인증없이도 접근 가능한 URL 패턴
                new HttpRequest(HttpMethod.OPTIONS, "/api/login"),
                new HttpRequest(HttpMethod.POST, "/api/login"),
                new HttpRequest(HttpMethod.GET, "/api/login/**"),
                new HttpRequest(HttpMethod.POST, "/api/logout"),
                new HttpRequest(HttpMethod.POST, "/api/auth/refresh"),
                new HttpRequest(HttpMethod.POST, "/api/signup/**"),
                new HttpRequest(HttpMethod.GET, "/api/signup/**"),
                new HttpRequest(HttpMethod.GET, "/api/scrape/bank-rates/**"),
                new HttpRequest(HttpMethod.GET, "/api/most-searched-tickers/**"),
                new HttpRequest(HttpMethod.GET, "/api/bullish/top3"),
                new HttpRequest(HttpMethod.GET, "/api/fear-greed-index"),
                new HttpRequest(HttpMethod.GET, "/api/search/top5"),
                new HttpRequest(HttpMethod.GET, "/api/stock/**")
        );
    }

    /* ---------HTTP 요청이 들어올 때마다 실행되는 필터 메소드--------*/
    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더에서 Authorization 헤더 값을 가져옴
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        //accessToken 이 없는 경우
        if(isTokenEmpty(accessToken)){
            sendUnauthorizedResponse(response, "로그인을 해주세요.");
            return; // 더 이상 필터 체인을 이어가지 않음
        }
        // 2. accessToken 이 유효한 경우
        if (jwtUtil.isValidAccessToken(accessToken)) {
            log.info("[JwtFilter] accessToken 유효");
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(accessToken);
            try{
                Authentication authentication = jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch(AuthenticationException e){
                log.warn("JWT 인증 실패 : {}", e.getMessage());
            }
        }
        // 3. accessToken 이 유효하지 않고 refreshToken 이 유효한 경우
        if (jwtUtil.isValidRefreshToken(refreshToken)) {
            log.info("[JwtFilter] accessToken 만료, refreshToken 유효");
            String newAccessToken = jwtUtil.reissueAllTokens(response, refreshToken);
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(newAccessToken);
            try{
                Authentication authentication = jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch(AuthenticationException e){
                log.warn("JWT 인증 실패 : {}", e.getMessage());
            }
        }else{ // refreshToken 이 무효한 경우
            sendUnauthorizedResponse(response, "로그인을 해주세요.");
            return;
        }
        filterChain.doFilter(request, response);
    }
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        //log.info("Request URI : {}", requestURI);
        // log.info("Request Method : {}", method);
        return isInWhiteList(method, requestURI);
    }
    private boolean isInWhiteList(String method, String url){
        AntPathMatcher antPathMatcher = new AntPathMatcher(); //url 패턴을 매칭할 때 사용

        return whiteList.stream()
                .anyMatch(white -> white.method().matches(method) &&
                        antPathMatcher.match(white.urlPattern(), url));
    }
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
    private boolean isTokenEmpty(String token){
        return token == null || token.isBlank();
    }

}
