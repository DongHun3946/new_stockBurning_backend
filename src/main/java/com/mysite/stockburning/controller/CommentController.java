package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.dto.CommentDTO;
import com.mysite.stockburning.dto.PostAndCommentsDTO;
import com.mysite.stockburning.dto.PostDTO;
import com.mysite.stockburning.dto.request.CommentCreateRequest;
import com.mysite.stockburning.entity.Comments;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.repository.PostRepository;
import com.mysite.stockburning.service.CommentService;
import com.mysite.stockburning.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final PostRepository postRepository;
    @GetMapping("/{postId}")
    public ResponseEntity<?> getComment(@PathVariable("postId") Long postId){
        try{
            List<Comments> comments = commentService.getCommentsByPostId(postId);
            Posts post = postRepository.findById(postId).orElse(null);

            PostDTO postDTO = PostDTO.getFromEntity(post);
            List<CommentDTO> commentDTOList = CommentDTO.getFromEntity(comments);
            PostAndCommentsDTO postAndCommentsDTO = new PostAndCommentsDTO(postDTO,commentDTOList);
            return ResponseEntity.ok(postAndCommentsDTO);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{postId}")
    public ResponseEntity<Void> createComment(@RequestBody CommentCreateRequest request,
                                           @PathVariable("postId") Long postId,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(customUserDetails != null){
                commentService.create(request, postId, customUserDetails);
            }
            if(customOAuth2User != null){
                commentService.create(request, postId, customOAuth2User);
            }
            return ResponseEntity.status(200).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteComment(){
        try{
            return ResponseEntity.status(200).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modify")
    public ResponseEntity<Void> modifyComment(){
        try{
            return ResponseEntity.status(200).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/like/{commentId}")
    public ResponseEntity<Void> likeComment(){
        try{
            return ResponseEntity.status(200).build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
