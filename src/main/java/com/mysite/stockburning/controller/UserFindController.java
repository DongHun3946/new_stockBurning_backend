package com.mysite.stockburning.controller;

import com.mysite.stockburning.dto.request.EmailCodeRequest;
import com.mysite.stockburning.dto.request.FindPwRequest;
import com.mysite.stockburning.dto.response.EmailResponse;
import com.mysite.stockburning.dto.response.UserIdResponse;
import com.mysite.stockburning.service.EmailService;
import com.mysite.stockburning.service.UserService;
import com.mysite.stockburning.util.TempPwdGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/find")
public class UserFindController {
    private final UserService userService;
    private final EmailService emailService;
    private final TempPwdGenerator tempPwdGenerator;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long CODE_EXPIRATION_TIME = 180;

    @GetMapping("/userId")
    public ResponseEntity<?> findId(@RequestParam("email") String email){
        String userid = userService.getUserId(email);
        return ResponseEntity.ok(userid);
    }
    @PostMapping("/userPw")
    public ResponseEntity<?> findPw(@RequestBody FindPwRequest request) throws MessagingException, UnsupportedEncodingException {
        boolean isValidUserInfo = userService.isEmailAndIdValid(request.userid(), request.email());
        if(isValidUserInfo){
            String tempPw = tempPwdGenerator.generateTempPasswd();
            emailService.sendTempPasswd(request.email(), tempPw);
            userService.modifyPassword(request.userid(), tempPw);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    @GetMapping("/userId/check-email") //이메일 확인 후 인증코드 보내는 컨트롤러
    public ResponseEntity<EmailResponse> checkEmail(@RequestParam("email") String email){
        try{
            if(!userService.isEmailDuplication(email)) {
                return ResponseEntity.ok(new EmailResponse(false, "존재하지 않는 이메일입니다."));
            }
            redisTemplate.delete(email);
            String code = emailService.generateCode(); //인증코드 생성
            emailService.sendEmail(email, code);       //사용자 이메일로 인증코드 보냄
            redisTemplate.opsForValue().set(email,code, CODE_EXPIRATION_TIME, TimeUnit.SECONDS);
            return ResponseEntity.ok(new EmailResponse(true, "인증코드가 전송되었습니다."));
        }catch(MessagingException | UnsupportedEncodingException e){
            log.error("이메일 인증코드 전송에 오류 발생:" ,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailResponse(false,"서버 오류 발생"));
        }
    }
    @PostMapping("/{type}/verify-code") //인증코드 확인하는 컨트롤러
    public ResponseEntity<EmailResponse> verifyCode(@PathVariable String type,
                                                    @RequestBody EmailCodeRequest request){
        try{
            if(type.equals("userPw") || type.equals("userId")){
                String storedCode = redisTemplate.opsForValue().get(request.email());
                if(storedCode.equals(request.code())){
                    redisTemplate.delete(request.email());
                    return ResponseEntity.ok(new EmailResponse(true, "인증 성공"));
                }
                else{
                    return ResponseEntity.ok(new EmailResponse(false, "인증코드가 일치하지 않습니다."));
                }
            }else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }catch(Exception e){
            log.error("이메일 인증코드 확인에 오류 발생:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailResponse(false, "서버 오류 발생"));
        }
    }
    @GetMapping("/userPw/check-userInfo") //이메일 확인 후 인증코드 보내는 컨트롤러
    public ResponseEntity<EmailResponse> checkEmailAndUserid(@RequestParam("userid") String userid,
                                                             @RequestParam("email") String email){
        try{
            if(!userService.isEmailAndIdValid(userid, email)) {
                return ResponseEntity.ok(new EmailResponse(false, "존재하지 않는 유저정보 입니다."));
            }
            String code = emailService.generateCode();
            emailService.sendEmail(email, code);
            redisTemplate.opsForValue().set(email, code, CODE_EXPIRATION_TIME, TimeUnit.SECONDS);
            return ResponseEntity.ok(new EmailResponse(true, "인증코드가 전송되었습니다."));
        }catch(Exception e){
            log.error("이메일 인증코드 전송에 오류 발생:" ,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailResponse(false,"서버 오류 발생"));
        }
    }
}
