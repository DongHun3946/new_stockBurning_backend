package com.mysite.stockburning.controller;

import com.mysite.stockburning.dto.request.SignupRequest;
import com.mysite.stockburning.dto.response.NickNameResponse;
import com.mysite.stockburning.dto.response.UserIdResponse;
import com.mysite.stockburning.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/signup")
public class UserSignupController {
    private final UserService userService;

    @PostMapping //회원가입 (회원 생성 로직)
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request){
        return ResponseEntity.created(URI.create("/users/" + userService.createUser(request))).build();
    }
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickNameDuplicated(@RequestParam("nickname") String nickName){
        try{
            if (userService.isNickNameDuplication(nickName)) {
                return ResponseEntity.ok()
                        .body(new NickNameResponse(false, "이미 존재하는 닉네임입니다."));
            } else {
                return ResponseEntity.ok()
                        .body(new NickNameResponse(true, "사용 가능한 닉네임입니다."));
            }
        } catch(Throwable e){
            log.error("닉네임 중복확인에 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //500 오류
                    .body(new NickNameResponse(false, "서버 오류가 발생했습니다. 다시 시도해주세요."));
        }
    }
    @GetMapping("/check-userid")
    public ResponseEntity<?> checkUserIdDuplicated(@RequestParam("userid") String userId){
        try{
            if (userService.isUserIdDuplication(userId)) {
                return ResponseEntity.ok()
                        .body(new UserIdResponse(false, "이미 존재하는 아이디입니다."));
            } else {
                return ResponseEntity.ok()
                        .body(new UserIdResponse(true, "사용 가능한 아이디입니다."));
            }
        } catch(Throwable e){
            log.error("아이디 중복확인에 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //500 오류
                    .body(new UserIdResponse(false, "서버 오류가 발생했습니다. 다시 시도해주세요."));
        }
    }
}


/*
@Valid : DTO의 유효성 검사 (@NotBlank, @Size 등의 어노테이션)

@RequestBody : HTTP 요청의 본문을 SignupRequestDTO 객체로 변환

ResponseEntity.created(URI uri) : HTTP 201 Created 상태 코드를 응답으로 반환하는 메소드
(HTTP 201 코드는 새로운 리소스가 성공적으로 생성되었음을 의미)

ResponseEntity<?> 는 응답 본문에 어떤 타입의 데이터든지 담을 수 있도록 한다.
ResponseEntity<Void>는 HTTP 의 본문이 비어있다는 의미
*/