package com.mysite.stockburning.authentication;

import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("[CustomUserDetailsService] - loadUserByUsername 메소드 실행");

        Users user = this.userRepository.findByUserId(userId)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


        log.info("[CustomUserDetailsService] - CustomUserDetails 객체 반환");
        // CustomUserDetails 객체 생성
        return new CustomUserDetails(
                user.getId(),
                user.getNickName(),
                user.getUserId(),
                user.getUserPw(),
                user.getRole(),
                user.getProviderType()
        );

    }
}

