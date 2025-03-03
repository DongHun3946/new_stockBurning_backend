package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.Comments;
import com.mysite.stockburning.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByPosts(Posts posts);
}
