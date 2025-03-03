package com.mysite.stockburning.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record SignupRequest(
        @Schema(description = "닉네임", example = "화가난라이언")
        @NotBlank(message = "닉네임이 입력되지 않았습니다.")
        @Size(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
        String nickName,

        @Schema(description = "아이디", example = "StevePP22")
        @NotBlank(message = "아이디가 입력되지 않았습니다.")
        @Size(max = 20, message = "아이디는 20자 이하로 입력해주세요.")
        String userId,

        @Schema(description = "비밀번호", example = "@testPW123123")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=]).*$", message = "비밀번호는 영문, 숫자, 특수문자를 각각 포함해야 합니다.")
        @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
        @Size(min = 8, max = 25, message = "비밀번호는 8~25자 사이로 입력해주세요.")
        String userPw,

        @Schema(description = "이메일", example = "testIMALE22@naver.com")
        @NotBlank(message = "이메일이 입력되지 않았습니다.")
        @Email
        String email
) { }
