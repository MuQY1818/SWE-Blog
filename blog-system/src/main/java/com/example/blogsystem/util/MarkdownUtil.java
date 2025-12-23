/*
 * Copyright (c) 2024 Weijue. All rights reserved.
 */
package com.example.blogsystem.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Markdown 格式转换工具类
 *
 * @author Weijue
 */
@Component
public class MarkdownUtil {

    private final Parser parser;
    private final HtmlRenderer renderer;

    /**
     * 构造函数，初始化 Markdown 解析器和渲染器
     */
    public MarkdownUtil() {
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        this.parser = Parser.builder()
                .extensions(extensions)
                .build();
        this.renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .escapeHtml(true)
                .sanitizeUrls(true)
                .build();
    }

    /**
     * 将 Markdown 文本转换为 HTML
     *
     * @param markdown Markdown 格式的文本
     * @return 渲染后的 HTML 字符串
     */
    public String markdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
