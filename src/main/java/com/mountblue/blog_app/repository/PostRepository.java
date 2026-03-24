package com.mountblue.blog_app.repository;

import com.mountblue.blog_app.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id")
    Post findByIdWithComments(Long id);

    @Query("SELECT DISTINCT p.user.fullName FROM Post p WHERE p.isPublished = true")
    List<String> findDistinctAuthors();

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t " +
            "WHERE p.isPublished = true " +
            "AND (:searchIsNull = true OR (" +
            "  LOWER(p.title)   LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "  LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "  LOWER(p.user.fullName)  LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "  LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "  LOWER(t.name)    LIKE LOWER(CONCAT('%', :search, '%')))) " +
            "AND (:authorIsNull = true OR p.user.fullName = :author) " +
            "AND (:tagIdsIsNull = true OR t.id IN :tagIds) " +
            "AND (:fromIsNull = true OR p.publishedAt >= :from) " +
            "AND (:toIsNull = true OR p.publishedAt <= :to)")
    Page<Post> findPosts(@Param("search") String search,
                         @Param("searchIsNull") boolean searchIsNull,
                         @Param("author") String author,
                         @Param("authorIsNull") boolean authorIsNull,
                         @Param("tagIds") List<Long> tagIds,
                         @Param("tagIdsIsNull") boolean tagIdsIsNull,
                         @Param("from") LocalDateTime from,
                         @Param("fromIsNull") boolean fromIsNull,
                         @Param("to") LocalDateTime to,
                         @Param("toIsNull") boolean toIsNull,
                         Pageable pageable);
}
