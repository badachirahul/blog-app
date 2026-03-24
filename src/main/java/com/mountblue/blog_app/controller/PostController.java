package com.mountblue.blog_app.controller;

import com.mountblue.blog_app.entity.Comment;
import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.entity.User;
import com.mountblue.blog_app.repository.UserRepository;
import com.mountblue.blog_app.service.PostService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/blog/posts")
public class PostController {

    private PostService postService;
    private UserRepository userRepository;

    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping("/new")
    public String showCreateBlogPage(Model model, Principal principal) {
        model.addAttribute("post", new Post());
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("loggedInUser", user.getFullName());
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
        model.addAttribute("comment", new Comment());
        return "viewBlog";
    }

    @GetMapping("/update/{id}")
    public String showUpdatePostPage(@PathVariable Long id, Model model) {
        Post post = postService.getSinglePost(id);
        model.addAttribute("post", post);

        // check ownership before showing form
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = post.getUser().getUsername().equals(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return "redirect:/error/403";
        }

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
            return "redirect:/blog/posts/" + post.getId();
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/blog/home";
    }
}





