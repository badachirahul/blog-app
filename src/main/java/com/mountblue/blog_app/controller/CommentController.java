package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Comment;
import com.mountblue.blog_app.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog/posts/{postId}/comments")
public class CommentController {

    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String saveComment(@PathVariable Long postId,
                              @ModelAttribute Comment comment) {
        commentService.saveComment(postId, comment);
        return "redirect:/blog/posts/{postId}";
    }
}