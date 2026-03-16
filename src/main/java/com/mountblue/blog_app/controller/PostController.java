package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String savePost(@Valid @ModelAttribute Post post,
                          BindingResult result,
                          @RequestParam String tagNames,
                          @RequestParam String action) {

        if(result.hasErrors()) {
            return "createPost";
        }
        else {
            postService.savePost(post, tagNames, action);
            return "redirect:/blog/home";
        }
    }

    @GetMapping("{id}")
    public String showSinglePost(@PathVariable Long id, Model model) {
        Post post = postService.getSinglePost(id);
        model.addAttribute("post", post);
        return "viewBlog";
    }

    @GetMapping("/update/{id}")
    public String updatePost(@PathVariable Long id, Model model) {
        Post post = postService.getSinglePost(id);
        model.addAttribute("post", post);
        return "updatePost";
    }

    @PostMapping("/update")
    public String saveUpdatedPost(@Valid @ModelAttribute Post post,
                                  BindingResult result,
                                  @RequestParam String tagNames) {

        if (result.hasErrors()) {
            return "updatePost";
        }
        else {
            postService.updatePost(post, tagNames);
            return "redirect:/blog/home";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/blog/home";
    }
}





