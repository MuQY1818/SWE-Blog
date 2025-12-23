/*
 * Copyright (c) 2024 Weijue. All rights reserved.
 */
package com.example.blogsystem.repository;

import com.example.blogsystem.entity.Post;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文章数据访问仓储接口
 *
 * @author Weijue
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
