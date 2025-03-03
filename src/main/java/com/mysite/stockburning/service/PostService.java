package com.mysite.stockburning.service;

import com.mysite.stockburning.authentication.CustomOAuth2User;
import com.mysite.stockburning.dto.request.PostCreateRequest;
import com.mysite.stockburning.dto.request.PostUpdateRequest;
import com.mysite.stockburning.dto.PostDTO;
import com.mysite.stockburning.entity.PostLikes;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.entity.StockTickers;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.repository.PostLikeRepository;
import com.mysite.stockburning.repository.PostRepository;
import com.mysite.stockburning.repository.StockRepository;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.authentication.CustomUserDetails;
import com.mysite.stockburning.util.Opinion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final StockSearchService stockService;
    private final StockRepository stockRepository;
    private final StockOpinionService stockOpinionService;
    private final S3Service s3Service;
    @Transactional
    public void create(final PostCreateRequest request, CustomUserDetails customUserDetails, String imagePath){
        Posts post = null;
        Users user =  userRepository.findById(customUserDetails.getId()).orElse(null);

        if(request.getStockSymbol()==null){ //티커 입력하지 않은 디폴트 상태일 때
            post = Posts.of(user, request.getContent(), imagePath, request.getOpinion());
        }else {                             //주식 티커 입력 후 작성할 때
            StockTickers stockTickers = stockService.getStockByTicker(request.getStockSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 stock ticker 입니다 : " + request.getStockSymbol()));

            post = Posts.of(user, request.getContent(), imagePath, request.getOpinion(), stockTickers);
        }
        if(request.getStockSymbol()!=null){ //티커 입력 후
            stockOpinionService.setRealtimePostCount(request.getStockSymbol());
            if(request.getOpinion()!=null){
                if(request.getOpinion().equals(Opinion.UP)) {
                    stockOpinionService.setRealtimeBullishOpinion(request.getStockSymbol());
                }else{
                    stockOpinionService.setRealtimeBearishOpinion(request.getStockSymbol());
                }
            }
        }else{ //디폴트 상태
            stockOpinionService.setRealtimePostCount();
            if(request.getOpinion()!=null){
                if(request.getOpinion().equals(Opinion.UP)) {
                    stockOpinionService.setRealtimeBullishOpinion();
                }else{
                    stockOpinionService.setRealtimeBearishOpinion();
                }
            }
        }
        assert post != null;       //post 가 null 이 아니면 그 다음 문장 실행
        this.postRepository.save(post);
    }
    @Transactional
    public void create(final PostCreateRequest request, CustomOAuth2User customOAuth2User, String imagePath){
        Posts post = null;
        Users user = userRepository.findById(customOAuth2User.getId()).orElse(null);

        if(request.getStockSymbol()==null){ //티커 입력하지 않은 디폴트 상태일 때
            post = Posts.of(user, request.getContent(), imagePath, request.getOpinion());
        }else {                             //주식 티커 입력 후 작성할 때
            StockTickers stockTickers = stockService.getStockByTicker(request.getStockSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 stock ticker 입니다 : " + request.getStockSymbol()));

            post = Posts.of(user, request.getContent(), imagePath, request.getOpinion(), stockTickers);
        }
        if(request.getStockSymbol()!=null){ //티커 입력 후
            stockOpinionService.setRealtimePostCount(request.getStockSymbol());
            if(request.getOpinion()!=null){
                if(request.getOpinion().equals(Opinion.UP)) {
                    stockOpinionService.setRealtimeBullishOpinion(request.getStockSymbol());
                }else{
                    stockOpinionService.setRealtimeBearishOpinion(request.getStockSymbol());
                }
            }
        }else{ //디폴트 상태
            stockOpinionService.setRealtimePostCount();
            if(request.getOpinion()!=null){
                if(request.getOpinion().equals(Opinion.UP)) {
                    stockOpinionService.setRealtimeBullishOpinion();
                }else{
                    stockOpinionService.setRealtimeBearishOpinion();
                }
            }
        }
        assert post != null;       //post 가 null 이 아니면 그 다음 문장 실행
        this.postRepository.save(post);
    }

    @Transactional
    public void update(Long postId, final PostUpdateRequest request, Long userid, String imagePath){
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if(post.getUsers().getId().equals(userid)){
            if(imagePath != null){
                s3Service.deleteS3Image(post.getImagePath());
                Posts updatedPost = post.toBuilder()
                        .content(request.getContent())
                        .opinion(request.getOpinion())
                        .imagePath(imagePath)
                        .build();
                this.postRepository.save(updatedPost);
            }else{
                Posts updatedPost = post.toBuilder()
                        .content(request.getContent())
                        .opinion(request.getOpinion())
                        .build();
                this.postRepository.save(updatedPost);
            }

        }else
            throw new IllegalArgumentException("작성한 글쓴이가 아닙니다.");

    }
    @Transactional
    public boolean delete(Long postId, Long userId) throws AccessDeniedException {
        Posts post = this.postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if(!post.getUsers().getId().equals(userId))
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        s3Service.deleteS3Image(post.getImagePath());
        this.postRepository.delete(post);
        return true;
    }
    @Transactional
    public void likePost(Long postId, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        postLikeRepository.findByPostsAndUsers(post, user)
                .ifPresentOrElse(
                        postLikeRepository::delete,  // 추천이 이미 되어 있다면 삭제 (취소)
                        () -> postLikeRepository.save(PostLikes.ofPost(post, user)) // 추천이 없다면 추가
                );
    }

    public int getLikeCount(Long postId){
        Posts post = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return postLikeRepository.countByPosts(post);
    }
    @Transactional(readOnly = true)
    public List<PostDTO> read(StockTickers ticker){
        List<Posts> allPosts = postRepository.findByStockTickersOrderByCreatedAtDesc(ticker);
        List<PostDTO> allPostDTO = new ArrayList<>();
        for(Posts post : allPosts){
            PostDTO postDTO = PostDTO.getFromEntity(post);
            allPostDTO.add(postDTO);
        }
        return allPostDTO;
    }
    @Transactional(readOnly = true)
    public List<PostDTO> readBestPost(String ticker){
        List<Posts> allPost = null;
        if(ticker.equals("null")){
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
            allPost = postRepository.findTopLikedPostsInLastWeek(oneWeekAgo);

        }else{
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
            StockTickers stockTickers = stockRepository.findByStockSymbol(ticker);
            allPost = postRepository.findTopLikedPostsInLastWeek(oneWeekAgo, stockTickers);
        }
        List<PostDTO> allPostDTO = new ArrayList<>();
        for(Posts post : allPost){
            PostDTO postDTO = PostDTO.getFromEntity(post);
            allPostDTO.add(postDTO);
        }
        return allPostDTO;
    }
    @Transactional(readOnly = true)
    public List<PostDTO> readDefault(){
        List<Posts> allPost = postRepository.findByStockTickersIsNullOrderByCreatedAtDesc();
        List<PostDTO> allPostDTO = new ArrayList<>();
        for(Posts post : allPost){
            PostDTO postDTO = PostDTO.getFromEntity(post);
            allPostDTO.add(postDTO);
        }
        return allPostDTO;
    }
    @Transactional(readOnly = true)
    public List<PostDTO> readLatestPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Posts> postsPage = postRepository.findByStockTickersIsNullOrderByCreatedAtDesc(pageable);

        return postsPage.getContent().stream()
                .map(post -> PostDTO.builder()
                        .id(post.getId())
                        .nickName(post.getUsers().getNickName())
                        .userId(post.getUsers().getUserId())
                        .content(post.getContent())
                        .user_id(post.getUsers().getId())
                        .user_imagePath(post.getUsers().getProfilePicture())
                        .post_imagePath(post.getImagePath())
                        .opinion(post.getOpinion())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .likes(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .build())
                .collect(Collectors.toList());
    }
}
