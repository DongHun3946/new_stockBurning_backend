package com.mysite.stockburning.controller;


import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserDeleteController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(HttpServletResponse response,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(customUserDetails == null && customOAuth2User == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
            Long id = null;
            if(customOAuth2User!=null){
                id = customOAuth2User.getId();
            }if(customUserDetails != null){
                id= customUserDetails.getId();
            }
            log.info("4444444");
            userService.deleteUser(id);
            log.info("5555555");
            jwtUtil.removeRefreshToken(response);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(Exception e){
            log.error("회원 탈퇴 중 에러발생 : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
