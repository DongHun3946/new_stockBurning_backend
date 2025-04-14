package com.mysite.stockburning.dto.response;

import com.mysite.stockburning.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ModifyResponse(
        String email
) {
    public static ModifyResponse getInfo(Users user){
        return ModifyResponse.builder()
                .email(user.getEmail())
                .build();
    }
}
