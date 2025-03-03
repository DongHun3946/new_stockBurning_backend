package com.mysite.stockburning.util;

import lombok.Getter;

@Getter
public enum UserRole {
    MANAGER("ROLE_MANAGER"),
    USER("ROLE_USER");

    private final String value;
    UserRole(String value) {
        this.value = value;
    }
}

/*
데이터베이스에서는 MANAGER, USER 로 저장
Spring Security 에서는 ROLE_MANAGER, ROLE_USER 로 사용됨
 */