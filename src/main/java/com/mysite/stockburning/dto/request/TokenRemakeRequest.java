package com.mysite.stockburning.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenRemakeRequest(
        @Schema(description = "로그인 시 발급받는 refresh 토큰")
        @NotBlank(message = "refresh 토큰은 비어있을 수 없습니다.")
        String refreshToken
) {
}
