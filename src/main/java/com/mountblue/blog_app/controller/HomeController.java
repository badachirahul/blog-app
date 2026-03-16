package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/blog")
public class HomeController {

    private PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/home")
    public String showHomePage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Post> posts = postService.getLatestPosts(page);

        model.addAttribute("currentPage", page);
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalPages", posts.getTotalPages());

        return "homePage";
    }
}
