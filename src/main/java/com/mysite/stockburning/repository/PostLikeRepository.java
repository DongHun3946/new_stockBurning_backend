package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.PostLikes;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikes, Long> {
    Optional<PostLikes> findByPostsAndUsers(Posts posts, Users users);
    int countByPosts(Posts posts);
}
