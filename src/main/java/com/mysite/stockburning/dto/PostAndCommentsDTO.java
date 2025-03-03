package com.mysite.stockburning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostAndCommentsDTO {
    private PostDTO postDTO;
    private List<CommentDTO> commentDTOList;
}
