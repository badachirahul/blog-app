package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/blog")
public class HomeController {

    private PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/home")
    public String showHomePage(@RequestParam(defaultValue = "1") int start,
                               @RequestParam(defaultValue = "10") int limit,
                               @RequestParam(defaultValue = "desc") String order,
                               @RequestParam(required = false) String author,
                               @RequestParam(required = false) List<Long> tagIds,
                               @RequestParam(required = false) String search,
                               Model model) {

        if (search != null && search.trim().isEmpty()) {
            search = null;
        }
        if (author != null && author.trim().isEmpty()) {
            author = null;
        }

        int page = (start - 1) / limit;

        Page<Post> posts = postService.getLatestPosts(page, limit, order, author, tagIds, search);

        model.addAttribute("currentPage", page);
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalPosts", posts.getTotalElements());
        model.addAttribute("start", start);
        model.addAttribute("limit", limit);
        model.addAttribute("order", order);
        model.addAttribute("selectedAuthor", author);
        model.addAttribute("selectedTagIds", tagIds != null ? tagIds : new ArrayList<>());
        model.addAttribute("authors", postService.getDistinctAuthors());
        model.addAttribute("tags", postService.getAllTags());
        model.addAttribute("search", search);

        return "homePage";
    }
}
