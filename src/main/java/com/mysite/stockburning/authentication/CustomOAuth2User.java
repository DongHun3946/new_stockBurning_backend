package com.mysite.stockburning.authentication;

import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

//OAuth2 로그인 후 사용자 정보를 담기 위해 사용

public class CustomOAuth2User implements OAuth2User {
    @Getter
    private final Long id;

    @Getter
    private final String nickName;

    @Getter
    private final Long providerId;

    @Getter
    private final ProviderType providerType;

    @Getter
    private final UserRole userRole;
    public CustomOAuth2User(Long id, String nickName, Long providerId){
        this.id = id;
        this.nickName = nickName;
        this.providerId = providerId;
        this.userRole = UserRole.USER;
        this.providerType = ProviderType.KAKAO;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.getValue()));
    }

    @Override
    public String getName() {
        return nickName;
    }

}
