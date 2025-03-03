package com.mysite.stockburning.dto;

import com.mysite.stockburning.entity.Comments;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.util.Opinion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostDTO {
    private Long id;
    private String nickName;
    private String userId;
    private String content;
    private Long user_id;
    private String user_imagePath;
    private String post_imagePath;
    private Opinion opinion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likes;
    private int commentCount;

    public static PostDTO getFromEntity(Posts posts){
        return PostDTO.builder()
                .id(posts.getId())
                .nickName(posts.getUsers().getNickName())
                .userId(posts.getUsers().getUserId())
                .content(posts.getContent())
                .user_id(posts.getUsers().getId())
                .user_imagePath(posts.getUsers().getProfilePicture())
                .post_imagePath(posts.getImagePath())
                .opinion(posts.getOpinion())
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .likes(posts.getLikeCount())
                .commentCount(posts.getCommentCount())
                .build();
    }
}
