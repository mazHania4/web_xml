package com.compi1.controller;

import com.compi1.model.stats.Report;

public class ReportsController {

    private FilesController files;
    public String of(Report report){
        StringBuilder resp = new StringBuilder();
        switch (report.getType()){
            case SITE_VISITS -> {
                resp.append("Visitas de cada sitio:\n");
                for (String p: report.getPaths()) {
                    resp.append(p).append(": ").append(files.getVisits(p)).append("\n");
                }
            }
            case PAGE_VISITS -> {
                resp.append("Visitas de cada pagina:\n");
                for (String p: report.getPaths()) {
                    String[] splits = p.split("\\.");
                    resp.append(p).append(": ").append(files.getVisits(splits[0], splits[splits.length-1])).append("\n");
                }
            }
            case POPULAR_PAGES -> {

            }
            case COMPONENT -> resp.append("Pendiente de implementar :(");
        }
        return resp.toString();
    }

    public ReportsController(FilesController files){
        this.files = files;
    }
}
