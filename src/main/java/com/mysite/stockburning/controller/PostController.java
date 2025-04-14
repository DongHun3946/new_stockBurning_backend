package com.mysite.stockburning.controller;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.dto.request.PostCreateRequest;
import com.mysite.stockburning.dto.request.PostUpdateRequest;
import com.mysite.stockburning.service.S3Service;
import com.mysite.stockburning.service.PostService;
import com.mysite.stockburning.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final S3Service s3Service;

    //@PreAuthorize("isAuthenticated()")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestPart("postData") PostCreateRequest request,
                                        @RequestPart(value="image", required = false) MultipartFile file,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {


        if (customUserDetails == null && customOAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("accessToken 재발급 요청");
        }

        if (request.getContent()== null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("게시글 내용은 필수입니다.");
        }
        try {
            String imagePath = null;
            if(file != null){
                imagePath = s3Service.uploadPostImage(file);
                log.info("S3 에 이미지 업로드 완료 : " + imagePath);
            }
            if(customUserDetails != null){
                postService.create(request, customUserDetails, imagePath);
            }
            if(customOAuth2User != null){
                postService.create(request, customOAuth2User, imagePath);
            }
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 작성 중 서버 오류가 발생했습니다.");
        }
    }
    //@PreAuthorize("isAuthenticated()")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{postId}")
    public ResponseEntity<?> modifyPost(@PathVariable("postId") Long postId,
                                        @RequestPart("postData") PostUpdateRequest request,
                                        @RequestPart(value="image", required = false) MultipartFile file,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        if (customUserDetails == null && customOAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("accessToken 재발급 요청");
        }
        if (request.getContent()== null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("게시글 내용은 필수입니다.");
        }
        try{
            String imagePath = null;
            if(file != null && request.getOriginalImage() == 0){
                imagePath = s3Service.uploadPostImage(file);
                log.info("S3 에 이미지 업로드 완료");
            }
            if(file == null && request.getOriginalImage() == 0 || file == null && request.getOriginalImage() == 1){
                imagePath = null;
            }
            if(customUserDetails != null){
                postService.update(postId, request, customUserDetails.getId(), imagePath);
            }
            if(customOAuth2User != null){
                postService.update(postId, request, customOAuth2User.getId(), imagePath);
            }

            return ResponseEntity.ok().build(); //200 OK
        }catch (Exception e){
            log.error("게시글 수정 중 에러발생 : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //@PreAuthorize("isAuthenticated()")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws AccessDeniedException { //spring security로 부터 사용자 정보 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isDeleted = false;
        if(customUserDetails != null){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            System.out.println("일반 로그인 사용자입니다: " + userDetails.getUsername());
            isDeleted = postService.delete(postId, userDetails.getId());
        }
        if(customOAuth2User != null){
            CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
            System.out.println("OAuth2 로그인 사용자입니다: " + oauth2User.getName());
            isDeleted = postService.delete(postId, oauth2User.getId());
        }

        if (isDeleted) {
            return ResponseEntity.ok().build(); //204 no content
        } else {
            return ResponseEntity.notFound().build(); //404 not found
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/like/{postId}")
    public ResponseEntity<Void> postLike(@PathVariable("postId") Long postId,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        try{
            if(customUserDetails != null){
                postService.likePost(postId, customUserDetails.getId());
            }
            if(customOAuth2User != null){
                postService.likePost(postId, customOAuth2User.getId());
            }

            return ResponseEntity.ok().build();
        }catch(Exception e){
            log.error("추천 기능 에러발생 : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/like/count/{postId}")
    public ResponseEntity<Integer> getLikes(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.getLikeCount(postId));
    }
}
