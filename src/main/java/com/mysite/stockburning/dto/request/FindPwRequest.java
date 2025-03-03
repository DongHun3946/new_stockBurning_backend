package com.mysite.stockburning.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FindPwRequest(
        @Schema(description = "사용자 아이디", example = "testUserid")
        @NotBlank(message = "아이디는 비어있을 수 없습니다.")
        String userid,

        @Schema(description = "사용자 이메일", example = "testEmail@naver.com")
        @NotBlank(message = "이메일은 비어있을 수 없습니다.")
        String email
) {
}
