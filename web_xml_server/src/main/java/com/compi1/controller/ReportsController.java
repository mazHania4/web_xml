package com.compi1.controller;

import com.compi1.model.sites.Component;
import com.compi1.model.sites.ComponentType;
import com.compi1.model.sites.Page;
import com.compi1.model.stats.Report;

import java.util.Map;

public class ReportsController {

    private FilesController files;
    public String of(Report report) throws RuntimeException{
        StringBuilder resp = new StringBuilder();
        switch (report.getType()){
            case SITE_VISITS -> {
                resp.append("Visitas de cada sitio:\n");
                for (String p: report.getPaths()) {
                    if (!files.pageIdExists(p)) throw new RuntimeException("ID de Pagina no encontrado: "+p);
                    resp.append(p).append(": ").append(files.getVisits(p)).append("\n");
                }
            }
            case PAGE_VISITS -> {
                resp.append("Visitas de cada pagina:\n");
                for (String p: report.getPaths()) {
                    String[] splits = p.split("\\.");
                    String site = splits[0];
                    String page = splits[splits.length-1];
                    if (!files.siteIdExists(site)) throw new RuntimeException("ID de Sitio no encontrado: "+site);
                    if (!files.pageIdExists(page)) throw new RuntimeException("ID de Pagina no encontrado: "+page);
                    resp.append(p).append(": ").append(files.getVisits(site, page)).append("\n");
                }
            }
            case POPULAR_PAGES -> {
                if (!files.siteIdExists(report.getPath())) throw new RuntimeException("ID de Sitio no encontrado: "+report.getPath());
                Map<String, Integer> pages = files.getPopularPages(report.getPath());
                resp.append("Paginas mas populares del sitio ").append(report.getPath()).append(":\n");
                for (Map.Entry<String, Integer> entry: pages.entrySet()) {
                    resp.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
            case COMPONENT -> {
                String[] splits = report.getPath().split("\\.");
                String site = splits[0];
                String pageStr = splits[splits.length-1];
                if (!files.siteIdExists(site)) throw new RuntimeException("ID de Sitio no encontrado: "+site);
                if (!files.pageIdExists(pageStr)) throw new RuntimeException("ID de Pagina no encontrado: "+pageStr);
                Page page = files.getPage(pageStr);
                if (report.getCompType().equals(ComponentType.ALL)){
                    int title = 0, paragraph = 0, img = 0, video = 0, menu = 0;
                    for (Component c: page.getComponents()) {
                        switch (c.getType()){
                            case TITLE -> title++;
                            case PARAGRAPH -> paragraph++;
                            case IMG -> img++;
                            case VIDEO -> video++;
                            case MENU -> menu++;
                        }
                    }
                    resp.append("Cantidad de componentes en ").append(report.getPath()).append(": \n")
                            .append("\tTITULO: ").append(title).append("\n")
                            .append("\tPARRAFO: ").append(paragraph).append("\n")
                            .append("\tIMAGEN: ").append(img).append("\n")
                            .append("\tVIDEO: ").append(video).append("\n")
                            .append("\tMENU: ").append(menu).append("\n");
                } else {
                    int i = 0;
                    for (Component c: page.getComponents()) {
                        if (c.getType().equals(report.getCompType())) i++;
                    }
                    resp.append("Cantidad de componentes tipo [").append(report.getCompType().name()).append("] en ").append(report.getPath()).append(": ").append(i);
                }
            }
        }
        return resp.toString();
    }

    public ReportsController(FilesController files){
        this.files = files;
    }
}
