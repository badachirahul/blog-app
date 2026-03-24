package com.mountblue.blog_app.service;

import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.entity.Tag;
import com.mountblue.blog_app.entity.User;
import com.mountblue.blog_app.repository.PostRepository;
import com.mountblue.blog_app.repository.TagRepository;
import com.mountblue.blog_app.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private PostRepository postRepository;
    private TagRepository tagRepository;
    private UserRepository userRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    public void savePost(Post post, String tagNames, String action) {
        String[] tagArray = tagNames.split(",");

        Set<Tag> tags = new HashSet<>();

        for (String name : tagArray) {
            name = name.trim().toLowerCase();

            Tag existingTag = tagRepository.findByName(name);

            if (existingTag == null) {
                Tag tag = new Tag();
                tag.setName(name);
                tagRepository.save(tag);
                tags.add(tag);
            } else {
                tags.add(existingTag);
            }
        }
        post.setTags(tags);

        if (post.getContent().length() > 100) {
            post.setExcerpt(post.getContent().substring(0, 100) + "....");
        }
        else {
            post.setExcerpt(post.getContent());
        }

        if (action.equals("publish")) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
        else {
            post.setPublished(false);
            post.setPublishedAt(null);
        }

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName);
        post.setUser(user);

        postRepository.save(post);
    }

    public Page<Post> getLatestPosts(int page, int limit, String order,
                                     String author, List<Long> tagIds,
                                     String search, LocalDateTime from, LocalDateTime to) {
        Sort sort = "asc".equals(order)
                ? Sort.by("publishedAt").ascending()
                : Sort.by("publishedAt").descending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        List<Long> safeTagIds = (tagIds == null || tagIds.isEmpty()) ? List.of(-1L) : tagIds;

        return postRepository.findPosts(search,     search == null,
                                        author,     author == null,
                                        safeTagIds, tagIds == null || tagIds.isEmpty(),
                                        from,       from == null,
                                        to,         to == null,
                                        pageable);
    }

    public List<String> getDistinctAuthors() {
        return postRepository.findDistinctAuthors();
    }

    public Post getSinglePost(Long id) {
        return postRepository.findByIdWithComments(id);
    }

    public void updatePost(Post updatedPost, String tagNames) {
        Post post = postRepository.findById(updatedPost.getId()).orElse(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        boolean isOwner = post.getUser().getUsername().equals(userName);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not allowed!");
        }

        String[] tagArray = tagNames.split(",");
        Set<Tag> tags = new HashSet<>();

        for (String name : tagArray) {
            name = name.trim().toLowerCase();

            if(name.isEmpty()) continue;

            Tag existingTag = tagRepository.findByName(name);

            if (existingTag == null) {
                Tag tag = new Tag();
                tag.setName(name);
                tagRepository.save(tag);
                tags.add(tag);
            } else {
                tags.add(existingTag);
            }
        }

        if (updatedPost.getContent().length() > 100) {
            post.setExcerpt(updatedPost.getContent().substring(0, 100) + "....");
        }
        else {
            post.setExcerpt(updatedPost.getContent());
        }

        post.setTags(tags);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    public void deletePost(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        Post post = postRepository.findById(id).orElse(null);

        boolean isOwner = post.getUser().getUsername().equals(userName);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not allowed!");
        }

        postRepository.deleteById(id);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
