package com.mountblue.blog_app.repository;

import com.mountblue.blog_app.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);
}
