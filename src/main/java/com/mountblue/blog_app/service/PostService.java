package com.mountblue.blog_app.service;

import com.mountblue.blog_app.entity.Post;
import com.mountblue.blog_app.entity.Tag;
import com.mountblue.blog_app.repository.PostRepository;
import com.mountblue.blog_app.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private PostRepository postRepository;
    private TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
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
        Post post = postRepository.findById(updatedPost.getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

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
        postRepository.deleteById(id);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
