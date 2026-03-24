package com.mountblue.blog_app.service;

import com.mountblue.blog_app.entity.Comment;
import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.repository.CommentRepository;
import com.mountblue.blog_app.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        System.out.println("saveComment called, comment id: " + comment.getId());

        Post post = postRepository.findById(postId).orElse(null);

        if (post != null) {
            comment.setPost(post);
            commentRepository.save(comment);
        }
    }

    public Comment getComment(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId);
    }

    public void updateComment(Long postId, Long commentId, Comment updatedComment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Post post = postRepository.findById(postId).orElse(null);

        boolean isOwner = post.getUser().getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not allowed!");
        }

        Comment comment = commentRepository.findByIdAndPostId(commentId, postId);
        comment.setName(updatedComment.getName());
        comment.setEmail(updatedComment.getEmail());
        comment.setMessage(updatedComment.getMessage());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        Post post = postRepository.findById(postId).orElse(null);

        boolean isOwner = post.getUser().getUsername().equals(userName);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not allowed!");
        }

        commentRepository.deleteByIdAndPostId(commentId, postId);
    }
}
