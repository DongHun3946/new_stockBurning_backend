package com.mysite.stockburning.service;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.dto.request.CommentCreateRequest;
import com.mysite.stockburning.entity.Comments;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.repository.CommentRepository;
import com.mysite.stockburning.repository.PostRepository;
import com.mysite.stockburning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public List<Comments> getCommentsByPostId(Long postId){
        Posts post = postRepository.findById(postId).orElse(null);
        if(post == null)
            return null;
        else
            return commentRepository.findByPosts(post);
    }
    public void create(final CommentCreateRequest request, Long postId, CustomUserDetails customUserDetails){
        Users user = userRepository.findById(customUserDetails.getId()).orElse(null);
        Posts post = postRepository.findById(postId).orElse(null);
        Comments comment = Comments.of(post, user, request.getContent());
        this.commentRepository.save(comment);
    }
    public void create(final CommentCreateRequest request, Long postId, CustomOAuth2User customOAuth2User){
        Users user = userRepository.findById(customOAuth2User.getId()).orElse(null);
        Posts post = postRepository.findById(postId).orElse(null);
        Comments comment = Comments.of(post, user, request.getContent());
        this.commentRepository.save(comment);
    }
    public void modify(){

    }
    public void delete(){

    }
}
