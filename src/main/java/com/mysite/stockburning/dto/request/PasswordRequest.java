package com.mysite.stockburning.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
        @Schema(description = "사용자 아이디", example = "testid")
        @NotBlank(message = "사용자 아이디는 비어있을 수 없습니다.")
        String userid,

        @Schema(description = "사용자 비밀번호", example = "testpw1234")
        @NotBlank(message = "사용자 비밀번호는 비어있을 수 없습니다.")
        String password
) {
}
