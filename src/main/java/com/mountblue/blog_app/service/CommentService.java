package com.mountblue.blog_app.service;

import com.mountblue.blog_app.entity.Comment;
import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.repository.CommentRepository;
import com.mountblue.blog_app.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public void saveComment(Long postId, Comment comment) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post != null) {
            comment.setPost(post);
            commentRepository.save(comment);
        }
    }
}
