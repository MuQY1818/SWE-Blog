/*
 * Copyright (c) 2024 Weijue. All rights reserved.
 */
package com.example.blogsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;

/**
 * 博客文章实体类
 *
 * @author Weijue
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Transient
    private String renderedContent;

    /**
     * 默认构造函数
     */
    public Post() {
    }

    /**
     * 获取文章ID
     *
     * @return 文章ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置文章ID
     *
     * @param id 文章ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取文章标题
     *
     * @return 文章标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置文章标题
     *
     * @param title 文章标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取文章内容
     *
     * @return 文章内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置文章内容
     *
     * @param content 文章内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取作者
     *
     * @return 作者名称
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 设置作者
     *
     * @param author 作者名称
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取渲染后的HTML内容
     *
     * @return 渲染后的HTML内容
     */
    public String getRenderedContent() {
        return renderedContent;
    }

    /**
     * 设置渲染后的HTML内容
     *
     * @param renderedContent 渲染后的HTML内容
     */
    public void setRenderedContent(String renderedContent) {
        this.renderedContent = renderedContent;
    }

    /**
     * 持久化前回调，自动设置创建时间
     */
    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}
