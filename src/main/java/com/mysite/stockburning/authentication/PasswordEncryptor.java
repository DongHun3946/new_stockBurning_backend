package com.mysite.stockburning.authentication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncryptor {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //평문 비밀번호를 Bcrypt 알고리즘을 통해 암호화
    public String encrypt(String password){
        return passwordEncoder.encode(password);
    }


    //사용자가 입력한 비밀번호와 암호화된 비밀번호가 일치하는지 확인
    public boolean matches(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
