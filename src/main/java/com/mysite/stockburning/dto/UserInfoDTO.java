package com.mysite.stockburning.dto;

import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserInfoDTO {
    private Long id;
    private String nickName;
    private String userId;
    private String profilePicture;
    private UserRole role;
    private ProviderType providerType;
}

