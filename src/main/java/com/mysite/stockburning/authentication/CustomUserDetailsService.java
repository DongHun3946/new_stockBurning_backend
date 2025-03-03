package com.mysite.stockburning.authentication;

import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.util.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findById(Long.valueOf(id));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Users siteUser = user.get();
        UserRole userRole = siteUser.getRole();
        // 권한 리스트 생성

        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("USER".equals(userRole.name())) {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        } else if ("MANAGER".equals(userRole.name())) {
            authorities.add(new SimpleGrantedAuthority(UserRole.MANAGER.getValue()));
        }

        // CustomUserDetails 객체 생성
        return new CustomUserDetails(
                siteUser.getId(),
                siteUser.getNickName(),
                siteUser.getUserId(),
                siteUser.getUserPw(),
                authorities
        );
    }
}
