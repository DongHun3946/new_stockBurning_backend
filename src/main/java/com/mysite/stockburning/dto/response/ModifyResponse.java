package com.mysite.stockburning.dto.response;

import com.mysite.stockburning.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ModifyResponse(
        @Schema(description = "사용자 이메일", example = "testEmail@naver.com")
        @NotBlank(message = "사용자 이메일은 비어있을 수 없습니다.")
        String email

) {
    public static ModifyResponse getInfo(Users user){
        return ModifyResponse.builder()
                .email(user.getEmail())
                .build();
    }
}
