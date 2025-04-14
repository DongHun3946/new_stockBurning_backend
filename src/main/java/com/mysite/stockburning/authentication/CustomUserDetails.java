package com.mysite.stockburning.authentication;

import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    @Getter
    private final Long id;
    @Getter
    private final String nickName;
    private final String userId;
    private final String userPw;

    @Getter
    private final ProviderType providerType;

    @Getter
    private final UserRole userRole;

    public CustomUserDetails(Long id, String nickName, String userId, String userPw, UserRole userRole, ProviderType providerType){
        this.id = id;
        this.nickName = nickName;
        this.userId = userId;
        this.userPw = userPw;
        this.userRole = userRole;
        this.providerType = providerType;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.getValue())); //Collections.singletonList : 리스트 한 개만 담긴 읽기 전용 리스트
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }
}
