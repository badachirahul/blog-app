package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Comment;
import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.service.CommentService;
import com.mountblue.blog_app.service.PostService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog/posts/{postId}/comments")
public class CommentController {

    private CommentService commentService;
    private PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping
    public String saveComment(@PathVariable Long postId,
                              @ModelAttribute Comment comment) {
        commentService.saveComment(postId, comment);
        return "redirect:/blog/posts/{postId}";
    }

    @GetMapping("/{commentId}/update")
    public String updateComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                Model model) {
        Post post = postService.getSinglePost(postId);
        Comment comment = commentService.getComment(postId, commentId);

        // check ownership before showing form
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = post.getUser().getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return "redirect:/error/403";
        }

        model.addAttribute("post", post);
        model.addAttribute("comment", comment);

        return "viewBlog";
    }

    @PostMapping("/{commentId}/update")
    public String saveUpdatedComment(@PathVariable Long postId,
                                     @PathVariable Long commentId,
                                     @ModelAttribute Comment comment) {
        commentService.updateComment(postId, commentId, comment);
        return "redirect:/blog/posts/{postId}";
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId) throws AccessDeniedException {
        commentService.deleteComment(postId, commentId);
        return "redirect:/blog/posts/{postId}";
    }
}