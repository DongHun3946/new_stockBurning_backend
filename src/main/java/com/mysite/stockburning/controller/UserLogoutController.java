package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.JwtProvider;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserLogoutController {
    private final JwtProvider jwtProvider;
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        try{
            jwtProvider.removeRefreshToken(response);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
