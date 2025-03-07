package com.mysite.stockburning.entity;

import com.mysite.stockburning.util.Opinion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Posts extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false) //외래키 컬럼의 이름을 명시적으로 설정
    private Users users;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true) //질문이 삭제되면 댓글도 삭제되도록
    private List<Comments> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLikes>  likes = new ArrayList<>();

    @Column(columnDefinition = "VARCHAR(255)")
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Opinion opinion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StockTickers_id", nullable = true) //외래키 설정
    private StockTickers stockTickers;

    public int getLikeCount(){
        if(likes != null)
            return likes.size();
        else
            return 0;
    }
    public int getCommentCount(){
        if(comments != null)
            return comments.size();
        else
            return 0;
    }

    public static Posts of(Users user, String content, String imagePath, Opinion opinion){
        return Posts.builder()
                .users(user)
                .content(content)
                .comments(null)
                .likes(null)
                .imagePath(imagePath)
                .opinion(opinion)
                .build();
    }
    public static Posts of(Users user, String content, String imagePath, Opinion opinion, StockTickers stockTickers){
        return Posts.builder()
                .users(user)
                .content(content)
                .comments(null)
                .likes(null)
                .imagePath(imagePath)
                .opinion(opinion)
                .stockTickers(stockTickers)
                .build();
    }
}
