package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.PostLikes;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.entity.StockTickers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> {
    List<Posts> findByStockTickersOrderByCreatedAtDesc(StockTickers stockTickers);
    List<Posts> findByStockTickersIsNullOrderByCreatedAtDesc();

    //첫 번째 요청 이후 마지막으로 불러온 게시글 id 를 기준으로 게시글 10개씩 불러옴
    List<Posts> findByStockTickersIsNullOrderByCreatedAtDesc(Long lastId, Pageable pageable);
    //첫 번째 요청에는 게시글 10개 불러옴
    Page<Posts> findByStockTickersIsNullOrderByCreatedAtDesc(Pageable pageable);
    @Query("SELECT p FROM Posts p where p.createdAt >= :oneWeekAgo AND p.stockTickers = :stockTickers ORDER BY SIZE(p.likes) DESC")
    List<Posts> findTopLikedPostsInLastWeek(LocalDateTime oneWeekAgo, StockTickers stockTickers);

    @Query("SELECT p FROM Posts p where p.createdAt >= :oneWeekAgo AND p.stockTickers IS NULL ORDER BY SIZE(p.likes) DESC")
    List<Posts> findTopLikedPostsInLastWeek(LocalDateTime oneWeekAgo);

}
