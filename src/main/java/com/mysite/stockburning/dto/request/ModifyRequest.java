package com.mysite.stockburning.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ModifyRequest(
        @Schema(description = "사용자 닉네임", example = "testNickName")
        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        String nickName,

        @Schema(description = "사용자 이메일", example = "testEmail@naver.com")
        @NotBlank(message = "이메일은 비어있을 수 없습니다.")
        String email
) {
}
