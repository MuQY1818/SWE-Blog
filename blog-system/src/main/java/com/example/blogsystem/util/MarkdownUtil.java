package com.example.blogsystem.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MarkdownUtil {

    private final Parser parser;
    private final HtmlRenderer renderer;

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

    public String markdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
