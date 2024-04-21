package com.compi1.controller;

import com.compi1.model.sites.Component;
import com.compi1.model.sites.Page;

import java.util.ArrayList;
import java.util.List;

public class HTMLWriter {

    private final FilesController files;
    private static String url = "http://localhost:2000/";

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
                case MENU -> addMenu(c, siteId, pageId, body);
            }
        }
        body.append("<h2>Pagina Padre:<h2>\n").append(link(siteId+"/"+page.getParentId(), page.getParentId()));
        body.append("<h2>Sub-paginas:<h2>\n");
        for (String p: page.getSubPageIds()) {
            body.append(link(siteId+"/"+p, p));
        }
        body.append("<h2>Tags:<h2>\n <ol>\n");
        for (String t: page.getTags()) {
            body.append("<li>").append(t).append("</li>\n");
        }
        body.append("<ol>\n");
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

    private void addMenu(Component c, String siteId, String pageId, StringBuilder body) {
        List<String> pageIds = new ArrayList<>();
        List<String> subpages = files.getSubPages(pageId);
        if (c.getTags().isEmpty()) pageIds = subpages;
        else {
            for (String tag: c.getTags()) {
                for (String p : files.getPageIdsWithTag(tag))
                    if (c.getParent().equals("-") || subpages.contains(p)) pageIds.add(p);
            }
        }
        body.append("<div>\n<h3>Menu:</h3>\n");
        for (String s : pageIds) {
            body.append(link(siteId+"/"+s, s));
        }
        body.append("</div>\n");
    }

    public String getSiteList() {
        StringBuilder body = new StringBuilder();
        body.append("<h1>Sitios:<h1>\n");
        for (String s: files.getSiteIds()) {
            body.append(link(s+"/"+s+"Index", s));
        }
        return formHtml(body.toString(), " ", "Sitios");
    }

    private String link(String route, String name){
        return "<a href =\""+url+route+"\">"+name+"</a><br>\n";
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
