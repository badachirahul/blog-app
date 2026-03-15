package com.mountblue.blog_app.repository;

import com.mountblue.blog_app.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);
}
