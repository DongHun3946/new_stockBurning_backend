package com.mysite.stockburning.dto;

import com.mysite.stockburning.entity.Comments;
import com.mysite.stockburning.entity.Posts;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CommentDTO {
    private Long id;
    private String nickName;
    private String content;
    private String user_imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static CommentDTO getFromEntity(Comments comments){
        return CommentDTO.builder()
                .id(comments.getId())
                .nickName(comments.getUsers().getNickName())
                .content(comments.getContent())
                .user_imagePath(comments.getUsers().getProfilePicture())
                .createdAt(comments.getCreatedAt())
                .updatedAt(comments.getUpdatedAt())
                .build();
    }
    public static List<CommentDTO> getFromEntity(List<Comments> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (Comments comment : comments) {
            commentDTOList.add(getFromEntity(comment));
        }
        return commentDTOList;
    }
}
