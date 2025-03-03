package com.mysite.stockburning.authentication;

import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.exception.UnauthorizedException;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class JwtProvider {
    private static final String USER_ID = "userId";
    private static final String USER_ROLE = "userRole";
    private static final String USER_NICK = "userNickName";
    private static final String USER_IMAGE = "userImage";
    private static final String PROVIDER_TYPE = "providerType";
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    private final String accessSecretKey;
    private final String refreshSecretKey;
    private final Long accessExpiration;
    private final Long refreshExpiration;

    public JwtProvider(CustomUserDetailsService customUserDetailsService, CustomOAuth2UserService customOAuth2UserService,@Value("${security.jwt.token.secret-key}") String accessSecretKey,
                       @Value("${security.jwt.refresh.secret-key}") String refreshSecretKey,
                       @Value("${security.jwt.token.expiration}") Long accessExpiration,
                       @Value("${security.jwt.refresh.expiration}") Long refreshExpiration) {
        this.customUserDetailsService = customUserDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.accessSecretKey = accessSecretKey;
        this.accessExpiration = accessExpiration;
        this.refreshSecretKey = refreshSecretKey;
        this.refreshExpiration = refreshExpiration;
    }

    //AccessToken 을 검증하고 Spring Security 의 인증 객체(Authentication)을 생성
    public Authentication getAuthentication(String accessToken){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessSecretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        Long id = Long.valueOf(claims.getSubject());
        String providerType = claims.get(PROVIDER_TYPE, String.class);

        if(providerType.equals("LOCAL")){
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(String.valueOf(id));
            return new UsernamePasswordAuthenticationToken(customUserDetails, null);
        }else if(providerType.equals("KAKAO")){
            CustomOAuth2User customOAuth2User = customOAuth2UserService.loadUserById(id);
            return new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), customOAuth2User.getName());
        }
        return null;
    }
    /*----------------------------JWT 토큰 생성------------------------------------*/
    private String createToken(Long id, String nickName, String userId, UserRole userRole, String image, String providerType, String secretKey, long expiration) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(id.toString()) //subject
                .claim(USER_ID, userId)
                .claim(USER_ROLE, userRole)
                .claim(USER_NICK, nickName)
                .claim(USER_IMAGE, image)
                .claim(PROVIDER_TYPE, providerType)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*----------TokenResponse 로 refreshToken, accessToken 토큰 리턴-----------*/
    public TokenResponse getToken(Long id, String nickName, String userId, UserRole userRole, String image, String providerType) {
        String accessToken = createToken(id, nickName, userId, userRole, image, providerType, accessSecretKey, accessExpiration);
        String refreshToken = createToken(id, nickName, userId, userRole, image, providerType, refreshSecretKey, refreshExpiration);
        return new TokenResponse(accessToken, refreshToken);
    }
    /* --------------------쿠키에서 refreshToken 가져오기 -------------------*/
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


    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);  // "Bearer " 제거
        }
        return null;
    }
    /*--- accessToken 이 유효한지 판단 ---*/
    public boolean isValidAccessToken(String accessToken){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(accessSecretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        }catch(SecurityException | MalformedJwtException e){
            log.warn("유효하지 않은 jwt : ", e);
        } catch(ExpiredJwtException e){
            log.warn("만료된 jwt : ", e);
        }catch(UnsupportedJwtException e){
            log.warn("지원하지 않는 jwt : ", e);
        }catch(IllegalArgumentException e){
            log.warn("jwt 클레임이 비어있음 : ", e);
        }
        return false;
    }

    /*--- refreshToken 이 유효한지 판단 ---*/
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(refreshSecretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch(SecurityException | MalformedJwtException e){
            log.warn("유효하지 않은 jwt : ", e);
        } catch(ExpiredJwtException e){
            log.warn("만료된 jwt : ", e);
        }catch(UnsupportedJwtException e){
            log.warn("지원하지 않는 jwt : ", e);
        }catch(IllegalArgumentException e){
            log.warn("jwt 클레임이 비어있음 : ", e);
        }
        return false;
    }
    public void removeRefreshToken(HttpServletResponse response){
        Cookie originalCookie = new Cookie("refreshToken", null);
        originalCookie.setHttpOnly(true);
        originalCookie.setSecure(false);
        originalCookie.setPath("/");
        originalCookie.setMaxAge(0);
        response.addCookie(originalCookie);
    }
    public Map<Object, Object> reissueAccessToken(HttpServletResponse response, String refreshToken){
        Map<Object, Object> userInfo = decode(refreshToken, refreshSecretKey);
        Long id = (Long) userInfo.get("id");
        String nickName = (String) userInfo.get("userNickName");
        String userId = (String) userInfo.get("userId");
        String userRoleString= (String) userInfo.get("userRole");
        UserRole userRole = UserRole.valueOf(userRoleString);
        String userImage = (String) userInfo.get("userImage");
        String providerType = (String) userInfo.get("providerType");

        TokenResponse tokenResponse = getToken(id, nickName, userId, userRole, userImage, providerType);
        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        return userInfo;
    }
    /*--- accessToken, refreshToken 재생성 ---*/
    public Map<Object, Object> reissueAllTokens(HttpServletResponse response,
                                 String refreshToken){
        Map<Object, Object> userInfo = decode(refreshToken, refreshSecretKey); //refreshToken 해독한 결과 가져옴
        Long id = (Long) userInfo.get("id");
        String nickName = (String) userInfo.get("userNickName");
        String userId = (String) userInfo.get("userId");
        String userRoleString= (String) userInfo.get("userRole");
        UserRole userRole = UserRole.valueOf(userRoleString);
        String userImage = (String) userInfo.get("userImage");
        String providerType = (String) userInfo.get("providerType");

        TokenResponse tokenResponse = getToken(id, nickName, userId, userRole, userImage, providerType); //새로운 토큰 생성

        //기존 refreshToken 삭제
        Cookie originalCookie = new Cookie("refreshToken", null);
        originalCookie.setHttpOnly(true);
        originalCookie.setSecure(false);
        originalCookie.setPath("/");
        originalCookie.setMaxAge(0);
        response.addCookie(originalCookie);

        //새로운 refreshToken, accessToken 생성
        Cookie cookie = new Cookie("refreshToken", tokenResponse.refreshToken());
        cookie.setHttpOnly(true);      //js 에서 쿠키에 접근할 수 없도록 함(xss 공격 방지)
        cookie.setSecure(false);        //https 연결에서만 쿠키 전송
        cookie.setPath("/");           //쿠키가 적용될 경로
        cookie.setMaxAge(60*60*24*30); //쿠키 유효 기간(30일)
        cookie.setAttribute("SameSite", "Lax"); //외부사이트에서 요청할 경우 쿠키를 전송하지 않도록 설정 (CSRF 공격 방지)
        response.addCookie(cookie);
        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        return userInfo;
    }
    private Map<Object, Object> decode(String token, String secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Long id = Long.valueOf(claims.getSubject());
            String nickName = (String) claims.get(USER_NICK);
            String userId = (String) claims.get(USER_ID);
            String userRole = (String) claims.get(USER_ROLE);
            String userImage = (String) claims.get(USER_IMAGE);
            String providerType = (String) claims.get(PROVIDER_TYPE);

            Map<Object, Object> response = new HashMap<>();
            response.put("id", id);
            response.put(USER_NICK, nickName);
            response.put(USER_ID, userId);
            response.put(USER_ROLE, userRole);
            response.put(USER_IMAGE, userImage);
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
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);      //js 에서 쿠키에 접근할 수 없도록 함(xss 공격 방지)
        cookie.setSecure(false);       //https 연결에서만 쿠키 전송 true -> https, false -> http
        cookie.setPath("/");           //쿠키가 적용될 경로
        cookie.setMaxAge(60*60*24*30); //쿠키 유효 기간(30일)
        cookie.setAttribute("SameSite", "Lax"); //Strict 외부사이트에서 요청할 경우 쿠키를 전송하지 않도록 설정 (CSRF 공격 방지)

        response.addCookie(cookie);
    }
}