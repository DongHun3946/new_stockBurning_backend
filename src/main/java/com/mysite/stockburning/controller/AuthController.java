package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.service.UserService;
import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    //ss
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        String accessToken = null;
        Map<Object, Object> userInfo = null;
        log.info("[AuthController] - 새로고침");
        if (refreshToken != null) {
            try{
                if (jwtUtil.isValidRefreshToken(refreshToken)) {
                    accessToken = jwtUtil.reissueAllTokens(response, refreshToken);
                } else {
                    accessToken = jwtUtil.reissueAccessToken(response, refreshToken);
                }
                userInfo = jwtUtil.decode(true, accessToken);
            }catch(Exception e){
                log.error("/api/auth/refresh 처리 도중 에러 발생 : ", e);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("토큰 검증 중 오류 발생");
            }
            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("유저 정보를 확인할 수 없습니다. 다시 로그인해주세요.");
            }
            Long id = (Long) userInfo.get("id");
            String nickName = String.valueOf(userInfo.get("userNickName"));
            UserRole role = UserRole.valueOf(String.valueOf(userInfo.get("userRole")));
            ProviderType type = ProviderType.valueOf(String.valueOf(userInfo.get("providerType")));
            String userId = userService.getUserId(id);
            String profilePicture = userService.getProfilePicture(id);

            UserInfoDTO userInfoDTO = new UserInfoDTO(id, nickName, userId, profilePicture, role, type);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userInfoDTO);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}