package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.authentication.JwtUtil;
import com.mysite.stockburning.dto.request.ModifyRequest;
import com.mysite.stockburning.dto.request.PasswordRequest;
import com.mysite.stockburning.dto.response.PasswordResponse;
import com.mysite.stockburning.dto.response.TokenResponse;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.service.S3Service;
import com.mysite.stockburning.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/modify")
public class UserModifyController {
    private final UserService userService;
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;
    @PutMapping("/user/{userId}")
    public ResponseEntity<Void> modifyUserInfo(HttpServletResponse response,
                                               @PathVariable("userId") String userid,
                                               @RequestBody ModifyRequest request){
        try{
            Users updatedUser = userService.modifyNickNameOrEmail(userid, request);
            setUserInfoAtJwt(response, updatedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(Exception e){
            log.error("개인정보 수정 에러 발생 ", e);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/user/profileImage")
    public ResponseEntity<Void> modifyUserImage(HttpServletResponse response,
                                                @RequestPart(value= "profileImage")MultipartFile file,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        if(customUserDetails == null && customOAuth2User == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
            String imagePath = null;
            Long userId = null;
            if(file!= null){
                imagePath = s3Service.uploadUserImage(file);
                log.info("S3 에 이미지 업로드 완료 : " + imagePath);
            }
            if(customUserDetails != null)
                userId = customUserDetails.getId();
            if(customOAuth2User != null)
                userId = customOAuth2User.getId();
            Users updatedUser = userService.modifyProfileImage(userId, imagePath);
            setUserInfoAtJwt(response, updatedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(Exception e){
            log.error("프로필 사진 수정 에러 발생 ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    @PutMapping("/password")
    public ResponseEntity<Void> modifyPassword(@RequestBody PasswordRequest request){
        try{
            userService.modifyPassword(request);
        }catch(Exception e){
            log.error("비밀번호 수정 에러 발생 ", e);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PostMapping("/check-password")
    public ResponseEntity<PasswordResponse> checkPassword(@RequestBody PasswordRequest request){
        boolean isValidPassword = userService.isPasswordValid(request.userid(), request.password());
        if(isValidPassword){
            return ResponseEntity.ok(new PasswordResponse(true, "인증되었습니다."));
        }else{
            return ResponseEntity.ok(new PasswordResponse(false, "일치하지 않습니다. 다시 확인해주세요"));
        }
    }
    private void setUserInfoAtJwt(HttpServletResponse response, Users updatedUser){
        TokenResponse tokenResponse = jwtUtil.getToken(updatedUser.getId(), updatedUser.getNickName(), updatedUser.getUserId(), updatedUser.getRole(), updatedUser.getProfilePicture(), String.valueOf(updatedUser.getProviderType()));
        response.setHeader("Authorization", "Bearer " + tokenResponse.accessToken());
        jwtUtil.setRefreshTokenCookie(response, tokenResponse.refreshToken());
    }
}
