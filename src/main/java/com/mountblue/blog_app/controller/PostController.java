package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog/posts")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/new")
    public String createBlogPage(Model model) {
        model.addAttribute("post", new Post());
        return "createPost";
    }

    @PostMapping()
    public String addPost(@ModelAttribute Post post,
                          @RequestParam String tagNames,
                          @RequestParam String action) {

        postService.addPost(post, tagNames, action);
        return "redirect:/blog/home";
    }
}





