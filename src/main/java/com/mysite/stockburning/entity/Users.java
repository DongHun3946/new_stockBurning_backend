package com.mysite.stockburning.entity;

import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String nickName;

    @Column(unique = true, length = 64)
    private String userId;

    @Column(unique = true, length = 64)
    private String userPw;

    @Column(unique = true)
    private String email;

    @Builder.Default
    private String profilePicture = "default_img";

    @Builder.Default
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posts> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLikes> postLikes = new ArrayList<>(); //수정

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER; // 역할 (default: USER)

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType providerType = ProviderType.LOCAL;

    private Long providerId;

    // 기본 생성자 (id는 null로 설정)
    public static Users of(String nickName, String userId, String userPw, String email) {
        return Users.builder()
                .nickName(nickName)
                .userId(userId)
                .userPw(userPw)
                .email(email)
                .build();
    }
    public static Users of(Long id, String nickName, String userId){
        return Users.builder()
                .id(id)
                .nickName(nickName)
                .userId(userId)
                .build();
    }
}


/*
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) UNIQUE,
    nick_name VARCHAR(30) UNIQUE,
    user_id VARCHAR(20) UNIQUE,
    user_pw VARCHAR(50)
    email VARCHAR(50) UNIQUE,
    profile_picture VARCHAR(255),
    role ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
*/