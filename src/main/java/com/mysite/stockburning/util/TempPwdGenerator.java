package com.mysite.stockburning.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TempPwdGenerator {
    private static final String CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int pwd_length = 10;
    public String generateTempPasswd(){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(pwd_length);
        for(int i=0;i<pwd_length;i++){
            int index = random.nextInt(CHAR.length());
            sb.append(CHAR.charAt(index));
        }
        return sb.toString();
   }
}
