package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.dto.request.LoginRequest;
import com.mysite.stockburning.dto.response.LoginResponse;
import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.service.UserService;
import com.mysite.stockburning.util.ProviderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserLoginController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /*
    @PostMapping("/login")
    public ResponseEntity<UserInfoDTO> login(@Valid @RequestBody LoginRequest request,
                                             HttpServletResponse response) {
        log.info("[UserLoginController] - 로그인 시도");
        //1. 인증 전 상태
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.userId(), request.userPw());

        try{
            log.info("[UserLoginController] - AuthenticationManager 에서 인증 시도");
            //2. 인증 수행
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            log.info("[UserLoginController] - DaoAuthenticationProvider 에서 객체를 반환받음");
            //3. 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("[UserLoginController] - SecurityContextHolder 에 객체 저장");

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            TokenResponse tokenResponse = jwtUtil.getToken(userDetails.getId(), userDetails.getNickName(), userDetails.getUserRole(), userDetails.getProviderType());

            response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
            jwtUtil.setRefreshToken(response, tokenResponse.refreshToken());
            String userId = userService.getUserId(userDetails.getId());
            String profilePicture = userService.getProfilePicture(userDetails.getId());
            UserInfoDTO userInfoDTO = new UserInfoDTO(userDetails.getId(), userDetails.getNickName(), userId, profilePicture, userDetails.getUserRole());
            return ResponseEntity.ok().body(userInfoDTO);
        }catch(AuthenticationException e){
            log.error("인증 실패 :  {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
    */

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
            jwtUtil.reissueAllTokens(response, refreshToken);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("토큰 재발급 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("토큰 재발급 중 에러: " + e.getMessage());
        }
    }
}