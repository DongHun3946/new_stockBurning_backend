package com.mysite.stockburning.authentication;

import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.exception.UnauthorizedException;
import com.mysite.stockburning.service.UserService;
import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;
    private final Long accessExpiration;
    private final Long refreshExpiration;
    private static final String USER_ROLE = "userRole";
    private static final String USER_NICK = "userNickName";
    private static final String PROVIDER_TYPE = "providerType";

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    private final UserService userService;
    public JwtUtil(CustomUserDetailsService customUserDetailsService,
                   CustomOAuth2UserService customOAuth2UserService,
                   @Value("${security.jwt.token.secret-key}") String accessSecretKey,
                   @Value("${security.jwt.refresh.secret-key}") String refreshSecretKey,
                   @Value("${security.jwt.token.expiration}") Long accessExpiration,
                   @Value("${security.jwt.refresh.expiration}") Long refreshExpiration, UserService userService) {
        this.customUserDetailsService = customUserDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.accessSecretKey = new SecretKeySpec(accessSecretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.accessExpiration = accessExpiration;
        this.refreshSecretKey = new SecretKeySpec(refreshSecretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.refreshExpiration = refreshExpiration;
        this.userService = userService;
    }

    //AccessToken 을 검증하고 Spring Security 의 인증 객체(Authentication)을 생성
    public Authentication getAuthentication(String accessToken){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        Long id = Long.valueOf(claims.getSubject());
        String userId = userService.getUserId(id);
        String providerType = claims.get(PROVIDER_TYPE, String.class);

        if(providerType.equals("LOCAL")){        // 자체 로그인 서비스를 이용한 경우
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userId);
            return new UsernamePasswordAuthenticationToken(customUserDetails, null);
        }else if(providerType.equals("KAKAO")){  //카카오 소셜 로그인 서비스를 이용한 경우
            CustomOAuth2User customOAuth2User = customOAuth2UserService.loadUserById(id);
            return new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), customOAuth2User.getName());
        }
        return null;
    }
    /*----------------------------JWT 토큰 생성------------------------------------*/
    private String createToken(Long id, String nickName, UserRole userRole, ProviderType providerType, SecretKey secretKey, long expiration) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(id.toString())   // 유저 ID(Long id)
                .claim(USER_ROLE, userRole)  // 유저 권한(USER)
                .claim(USER_NICK, nickName)  // 유저 닉네임(nickName)
                .claim(PROVIDER_TYPE, providerType) // 유저 로그인방식(LOCAL)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256) //HS256 알고리즘으로 JWT 서명하겠다는 의미
                .compact();
    }

    // TokenResponse 로 refreshToken, accessToken 토큰 리턴
    public TokenResponse getToken(Long id, String nickName, UserRole userRole, ProviderType providerType) {
        log.info("[JwtUtil] - accessToken, refreshToken 생성 완료");
        String accessToken = createToken(id, nickName, userRole, providerType, accessSecretKey, accessExpiration);
        String refreshToken = createToken(id, nickName,  userRole, providerType, refreshSecretKey, refreshExpiration);
        return new TokenResponse(accessToken, refreshToken);
    }
    // 쿠키에서 refreshToken 가져오기
    public String getRefreshTokenFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    // 요청 헤더로부터 accessToken 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);  // "Bearer " 제거
        }
        return null;
    }
    // accessToken 이 유효한지 판단
    public boolean isValidAccessToken(String accessToken){
        return isValidToken(accessToken, accessSecretKey);
    }

    // refreshToken 이 유효한지 판단
    public boolean isValidRefreshToken(String refreshToken) {
        return isValidToken(refreshToken, refreshSecretKey);
    }
    private boolean isValidToken(String token, SecretKey secretKey){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(SecurityException | MalformedJwtException e){
            log.warn("유효하지 않은 jwt", e);
        } catch(ExpiredJwtException e){
            log.info("만료된 jwt");
        }catch(UnsupportedJwtException e){
            log.warn("지원하지 않는 jwt", e);
        }catch(IllegalArgumentException e){
            log.warn("jwt 클레임이 비어있음", e);
        }
        return false;
    }
    // 로그아웃 시 refreshToken 제거
    public void removeRefreshToken(HttpServletResponse response){
        Cookie originalCookie = new Cookie("refreshToken", null);
        originalCookie.setHttpOnly(true);  //JavaScript 에서 접근 못하도록 설정
        originalCookie.setSecure(false);   //true : HTTPS 연결에서만 전송
        originalCookie.setPath("/");       // 루트 경로에 설정 -> 사이트 전체에서 이 쿠키가 유효
        originalCookie.setMaxAge(0);       // 만료 시간을 0으로 설정 -> 삭제 의도
        response.addCookie(originalCookie);
    }
    public void setRefreshToken(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);      //js 에서 쿠키에 접근할 수 없도록 함(xss 공격 방지)
        cookie.setSecure(false);       //https 연결에서만 쿠키 전송 true -> https, false -> http
        cookie.setPath("/");           //쿠키가 적용될 경로
        cookie.setMaxAge(60*60*24*30); //쿠키 유효 기간(30일)
        cookie.setAttribute("SameSite", "Lax"); //Strict : 외부사이트에서 요청할 경우 쿠키를 전송하지 않도록 설정 (CSRF 공격 방지)
        response.addCookie(cookie);
    }
    public String reissueAccessToken(HttpServletResponse response, String refreshToken){
        log.info("[JwtUtil] - accessToken 갱신 시도");
        Map<Object, Object> userInfo = decode(false, refreshToken);
        Long id = (Long) userInfo.get("id");
        String nickName = (String) userInfo.get(USER_NICK);
        UserRole userRole = UserRole.valueOf((String)userInfo.get(USER_ROLE));
        ProviderType providerType = ProviderType.valueOf((String)userInfo.get(PROVIDER_TYPE));

        TokenResponse tokenResponse = getToken(id, nickName, userRole, providerType);
        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        return tokenResponse.accessToken();
    }

    // accessToken, refreshToken 재생성
    public String reissueAllTokens(HttpServletResponse response,
                                   String refreshToken){
        log.info("[JwtUtil] - accessToken, refreshToken 갱신 시도");
        Map<Object, Object> userInfo = decode(false, refreshToken); //refreshToken 해독한 결과 가져옴
        Long id = (Long) userInfo.get("id");
        String nickName = (String) userInfo.get(USER_NICK);
        UserRole userRole = UserRole.valueOf((String)userInfo.get(USER_ROLE));
        ProviderType providerType =  ProviderType.valueOf((String)userInfo.get(PROVIDER_TYPE));

        TokenResponse tokenResponse = getToken(id, nickName,  userRole, providerType); //새로운 토큰 생성

        //기존 refreshToken 삭제
        removeRefreshToken(response);

        //새로운 refreshToken, accessToken 생성
        setRefreshToken(response, tokenResponse.refreshToken());

        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        return tokenResponse.accessToken();
    }

    //토큰 복호화
    public Map<Object, Object> decode(boolean isAccessToken, String token) {
        try {
            Claims claims = null;
            if(isAccessToken){
                claims = Jwts.parserBuilder()
                        .setSigningKey(accessSecretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            }else{
                claims = Jwts.parserBuilder()
                        .setSigningKey(refreshSecretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            }

            Long id = Long.valueOf(claims.getSubject());
            String nickName = (String) claims.get(USER_NICK);
            String userRole = (String) claims.get(USER_ROLE);
            String providerType = (String) claims.get(PROVIDER_TYPE);

            Map<Object, Object> response = new HashMap<>(); //순서 유지 x
            response.put("id", id);
            response.put(USER_NICK, nickName);
            response.put(USER_ROLE, userRole);
            response.put(PROVIDER_TYPE, providerType);
            return response;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("이미 만료된 토큰입니다.", e.getCause());
        } catch(io.jsonwebtoken.security.SignatureException e){
            throw new UnauthorizedException("secretKey 가 올바르지 않습니다.", e.getCause());
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.", e.getCause()); //e.getCause() : 원래 발생한 예외의 원인을 추출하여 새로운 예외에 전달
        }
    }
}