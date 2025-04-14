package com.mysite.stockburning.dto.response;

import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.util.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
        Long id,
        String nickName,
        String userId,
        String profileImageUrl,
        UserRole role


) {
        public static LoginResponse getInfo(Users user){
            return LoginResponse.builder()
                    .id(user.getId())
                    .nickName(user.getNickName())
                    .userId(user.getUserId())
                    .profileImageUrl(user.getProfilePicture())
                    .role(user.getRole())
                    .build();
        }
}
