package com.mountblue.blog_app.repository;

import com.mountblue.blog_app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findByIdAndPostId(Long commentId, Long postId);

    void deleteByIdAndPostId(Long commentId, Long postId);
}
