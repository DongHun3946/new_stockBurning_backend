package com.mysite.stockburning.authentication.authenticationFilter;


import com.mysite.stockburning.authentication.JwtProvider;
import com.mysite.stockburning.dto.request.HttpRequest;
import com.mysite.stockburning.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String USER_ID = "userId";
    private final JwtProvider tokenProvider;
    private final List<HttpRequest> whiteList;

    public JwtAuthenticationFilter(JwtProvider tokenProvider,
                                   @Value("${app.base.url}") String baseUrl){
        this.tokenProvider = tokenProvider;
        this.whiteList = List.of(
                new HttpRequest(HttpMethod.GET, "/**"),
                new HttpRequest(HttpMethod.POST, "/api/login/**"),
                new HttpRequest(HttpMethod.POST, "/api/signup/**"),
                new HttpRequest(HttpMethod.GET, "/api/login/**"),
                new HttpRequest(HttpMethod.GET, "/api/signup/**"),
                new HttpRequest(HttpMethod.GET, "/api/scrape/bank-rates/**"),
                new HttpRequest(HttpMethod.GET, "/api/most-searched-tickers/**"),
                new HttpRequest(HttpMethod.GET, "/api/stock/**")
        );
    }

    /* ---------요청 헤더에서 JWT 를 추출하고 검증하는 작업-----------*/
    /* ---------HTTP 요청이 들어올 때마다 실행되는 필터 메소드--------*/
    @Override
    public void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더에서 Authorization 헤더 값을 가져옴
        String accessToken = tokenProvider.getAccessTokenFromHeader(request);
        String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);

        //accessToken 이 없다면
        if(isTokenEmpty(accessToken)){
            sendUnauthorizedResponse(response, "로그인을 해주세요.");
            return; // 더 이상 필터 체인을 이어가지 않음
        }
        //accessToken 이 존재하며 유효할 때
        else if(!isTokenEmpty(accessToken) && tokenProvider.isValidAccessToken(accessToken)){
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            if(authentication != null){
                System.out.println("[JwtFilter] SecurityContextHolder 에 Authentication 저장");
                //log.info("[JwtFilter] SecurityContextHolder 에 Authentication 저장");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        //accessToken 이 유효하지 않을 때
        else if(!tokenProvider.isValidAccessToken(accessToken)){
            //refreshToken 이 유효할 때
            if(tokenProvider.isValidRefreshToken(refreshToken)){
                filterChain.doFilter(request, response);
            }
            //refreshToken 이 유효하지 않을 때
            else if(!tokenProvider.isValidRefreshToken(refreshToken)){
                sendUnauthorizedResponse(response, "세션이 만료되었습니다. 다시 로그인 해주세요.");
                return; // 더 이상 필터 체인을 이어가지 않음
            }
        }
        filterChain.doFilter(request, response);     //다음 필터로 요청을 전달
    }
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return isTokenEmpty(token) || isInWhiteList(method, requestURI);
    }
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
    private boolean isInWhiteList(String method, String url){
        AntPathMatcher antPathMatcher = new AntPathMatcher(); //url 패턴을 매칭할 때 사용

        return whiteList.stream()
                .anyMatch(white -> white.method().matches(method) &&
                        antPathMatcher.match(white.urlPattern(), url));
    }
    private boolean isTokenEmpty(String token){
        return token == null || token.isBlank();
    }

}
