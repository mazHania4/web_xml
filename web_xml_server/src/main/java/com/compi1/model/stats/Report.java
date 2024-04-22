package com.compi1.model.stats;

import com.compi1.model.sites.ComponentType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class Report {
    private String path;
    private List<String> paths;
    private ReportType type;
    private ComponentType compType;
}
