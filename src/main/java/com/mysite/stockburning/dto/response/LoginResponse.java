package com.mysite.stockburning.dto.response;

import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.util.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
        @Schema(description = "로그인한 유저의 기본키", example = "1")
        Long id,

        @Schema(description = "로그인한 유저의 닉네임", example = "화가난라이언")
        String nickName,

        @Schema(description = "로그인한 유저의 아이디", example = "testid")
        String userId,
        @Schema(description = "로그인한 유저의 프로필 이미지 url경로", example = "https://stockburning.kr/user1.png")
        String profileImageUrl,

        @Schema(description = "권한", example = "USER")
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
