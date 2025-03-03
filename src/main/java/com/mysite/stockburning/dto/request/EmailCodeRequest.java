package com.mysite.stockburning.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EmailCodeRequest(
        @Schema(description = "이메일", example = "testemail@naver.com")
        @NotBlank(message = "이메일은 비어있을 수 없습니다.")
        String email,
        @Schema(description = "이메일 인증코드", example = "441235")
        @NotBlank(message = "이메일 인증코드는 비어있을 수 없습니다.")
        String code
) {
}
