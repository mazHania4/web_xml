package com.compi1.controller;

import com.compi1.model.sites.Component;
import com.compi1.model.sites.Page;

import java.util.List;

public class HTMLWriter {

    private final FilesController files;

    public String parsePage(String pageId, String siteId) throws RuntimeException {
        Page page = files.getPage(pageId);
        StringBuilder body = new StringBuilder();
        StringBuilder styles = new StringBuilder();
        List<Component> comps = page.getComponents();
        for (Component c: comps) {
            switch (c.getType()){
                case TITLE -> addTitle(c, body, styles);
                case PARAGRAPH -> addParagraph(c, body, styles);
                case IMG -> addIMG(c, body, styles);
                case VIDEO -> addVideo(c, body, styles);
                case MENU -> addMenu(c, siteId, body);
            }
        }
        //add parent page
        //add subpages
        //add tags??
        return formHtml(body.toString(), styles.toString(), page.getTitle());
    }

    private void addTitle(Component c, StringBuilder body, StringBuilder styles) {
        body.append("<h1 id=\"").append(c.getId()).append("\">").append(c.getText()).append("</h1>\n");
        addParagraphTitleStyle(c, styles);
    }
    private void addParagraph(Component c, StringBuilder body, StringBuilder styles) {
        body.append("<p id=\"").append(c.getId()).append("\">").append(c.getText()).append("</p>\n");
        addParagraphTitleStyle(c, styles);
    }

    private void addParagraphTitleStyle(Component c, StringBuilder styles) {
        styles.append("#").append(c.getId()).append(" {\n");
        if (!c.getColor().isEmpty()) styles.append("color:").append(c.getColor()).append(";\n");
        if (c.getAlignment() != null) styles.append("text-align:").append(c.getAlignment().name().toLowerCase()).append(";\n");
        styles.append("}\n");
    }

    private void addIMG(Component c, StringBuilder body, StringBuilder styles) {
        body.append("<div ");
        if (c.getAlignment() != null) body.append("style=\"text-align:").append(c.getAlignment().name().toLowerCase()).append(";\"\n");
        body.append(">\n");
        body.append("<img id=\"").append(c.getId()).append("\" src=\"").append(c.getSrc()).append("\">\n").append("</div>\n");
        styles.append("#").append(c.getId()).append(" {\n");
        styles.append("width:").append(c.getWidth()).append("px;\n");
        styles.append("height:").append(c.getHeight()).append("px;\n");
        styles.append("}\n");
    }

    private void addVideo(Component c, StringBuilder body, StringBuilder styles) {
        body.append("<video id=\"").append(c.getId()).append("\" controls>\n");
        body.append("<source src=\"").append(c.getSrc()).append("\" type=\"video/mp4\" />");
        body.append("</video>");
        styles.append("#").append(c.getId()).append(" {\n");
        styles.append("width:").append(c.getWidth()).append("px;\n");
        styles.append("height:").append(c.getHeight()).append("px;\n");
        styles.append("}\n");
    }

    private void addMenu(Component c, String siteId, StringBuilder body) {
        List<String> pageIds = c.getTags().isEmpty() ?
                files.getSubPages(c.getParent()) :
                c.getTags().stream()
                        .flatMap(tag -> files.getPageIdsWithTag(tag).stream())
                        .distinct()
                        .filter(id -> c.getParent().isEmpty() || files.getSubPages(c.getParent()).contains(id))
                        .toList();
        body.append("<div>\n");
        for (String s : pageIds) {
            body.append("<a href=\"http://localhost:2000/").append(siteId).append("/").append(s).append("\">").append(s).append("</a><br>\n");
        }
        body.append("</div>\n");
    }

    public String getSiteList() {
        return "";
    }

    private String formHtml(String body, String styles, String title){
        return
        "<!DOCTYPE html>\n"+
        "<html>\n"+
        "\t<head>\n" +
        "\t\t<title>"+title+"</title>\n" +
        "\t\t<style>\n"+styles+"\n\t\t</style>\n" +
        "\t</head>\n"+
        "\t<body>"+body+"\n"+
        "\t</body>\n"+
        "</html>";
    }

    public HTMLWriter(FilesController files) {
        this.files = files;
    }
}
