package com.mysite.stockburning.authentication;

import com.mysite.stockburning.util.ProviderType;
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

    private final Collection<? extends GrantedAuthority> authorities;
    public CustomUserDetails(Long id, String nickName, String userId, String userPw, Collection<? extends GrantedAuthority> authorities){
        this.id = id;
        this.nickName = nickName;
        this.userId = userId;
        this.userPw = userPw;
        this.providerType = ProviderType.LOCAL;
        this.authorities = authorities;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
