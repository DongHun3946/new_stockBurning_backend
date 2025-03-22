package com.mysite.stockburning.controller;

import com.mysite.stockburning.dto.response.EmailResponse;
import com.mysite.stockburning.service.EmailService;
import com.mysite.stockburning.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class EmailController {
    private final UserService userService;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long CODE_EXPIRATION_TIME = 180;
    private final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @PostMapping("/send-email")
    public ResponseEntity<EmailResponse> sendVerificationEmail(@RequestParam("email") String email){
        try{
            if(userService.isEmailDuplication(email)) {
                return ResponseEntity.ok(new EmailResponse(false, "이미 존재하는 이메일입니다."));
            }
            String code = emailService.generateCode(); //인증코드 생성.
            emailService.sendEmail(email, code, "회원가입");       //사용자 이메일로 인증코드 보냄
            redisTemplate.opsForValue().set(email,code, CODE_EXPIRATION_TIME, TimeUnit.SECONDS);
            return ResponseEntity.ok(new EmailResponse(true, "인증코드가 전송되었습니다."));
        }catch(MessagingException | UnsupportedEncodingException e){
            logger.error("이메일 인증코드 전송에 오류 발생:" ,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailResponse(false,"서버 오류 발생"));
        }
    }
    @PostMapping("/verify-code")
    public ResponseEntity<EmailResponse> verifyCode(@RequestParam("email") String email,
                                                    @RequestParam("code") String code){
        try{
            String storedCode = redisTemplate.opsForValue().get(email);
            if(storedCode.equals(code)){
                redisTemplate.delete(email);
                return ResponseEntity.ok(new EmailResponse(true, "인증 성공"));
            }
            else{
                return ResponseEntity.ok(new EmailResponse(false, "인증코드가 일치하지 않습니다."));
            }
        }catch(Throwable e){
            logger.error("이메일 인증코드 확인에 오류 발생:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailResponse(false, "서버 오류 발생"));
        }
    }
}
