package com.mountblue.blog_app.repository;

import com.mountblue.blog_app.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByIsPublishedTrue(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id")
    Post findByIdWithComments(Long id);

    @Query("SELECT DISTINCT p.author FROM Post p WHERE p.isPublished = true")
    List<String> findDistinctAuthors();

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t " +
            "WHERE p.isPublished = true " +
            "AND (:author IS NULL OR p.author = :author) " +
            "AND (:tagIds IS NULL OR t.id IN :tagIds)")
    Page<Post> findByFilters(@Param("author") String author,
                             @Param("tagIds") List<Long> tagIds,
                             Pageable pageable);
}
