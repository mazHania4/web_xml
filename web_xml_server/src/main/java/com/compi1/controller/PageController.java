package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Param;
import com.compi1.model.actions.ParamType;
import com.compi1.model.sites.Page;

import java.time.LocalDate;
import java.util.ArrayList;

public class PageController {

    private final FilesController files;

    public void executeNEW(Action action) throws RuntimeException {
        System.out.println("\nNEW PAGE\n");
        validateNEW(action);
        String id = "", parentId="", title="", site="", cr_user="", cr_s_date="", mod_user="", mod_s_date="";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) id = p.getValue();
            if (p.getType().equals(ParamType.CREATION_USER)) cr_user = p.getValue();
            if (p.getType().equals(ParamType.CREATION_DATE)) cr_s_date = p.getValue();
            if (p.getType().equals(ParamType.MODIFICATION_USER)) mod_user = p.getValue();
            if (p.getType().equals(ParamType.MODIFICATION_DATE)) mod_s_date = p.getValue();
            if (p.getType().equals(ParamType.PARENT)) parentId = p.getValue();
            if (p.getType().equals(ParamType.PARAM_TITLE)) title = p.getValue();
            if (p.getType().equals(ParamType.SITE)) site = p.getValue();
        }
        if (files.pageIdExists(id)) throw new IllegalArgumentException("ID: "+id+" for pages already exists");
        if (cr_user.isEmpty()) cr_user = "-unknown-";
        if (mod_user.isEmpty()) cr_user = "-unknown-";
        if (parentId.equals("index")) parentId = site + "_index";
        if (title.isEmpty()) title = "title";
        LocalDate cr_date = SiteController.processDate(cr_s_date);
        LocalDate mod_date = SiteController.processDate(mod_s_date);
        Page page = Page.builder()
                .parentId(parentId)
                .id(id)
                .title(title)
                .components(new ArrayList<>())
                .subPageIds(new ArrayList<>())
                .tags(new ArrayList<>())
                .cr_user(cr_user)
                .cr_date(cr_date)
                .mod_user(mod_user)
                .mod_date(mod_date)
                .build();
        files.addPage(page, site);
    }

    public void executeMODIFY(Action action) throws RuntimeException {
        System.out.println("\nMODIFY PAGE\n");
        validateMODIFY(action);
        String id = "", title = "";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) id = p.getValue();
            if (p.getType().equals(ParamType.PARAM_TITLE)) title = p.getValue();
        }
        if (!files.pageIdExists(id)) throw new IllegalArgumentException("ID: "+id+" for pages not found");
        Page page = files.getPage(id);
        //verificar que no sea el index
        if (!title.isEmpty()) page.setTitle(title);
        if (!action.getTags().isEmpty()) page.setTags(action.getTags());
        files.rewritePage(page);
    }

    public void executeDELETE(Action action) throws RuntimeException {
        System.out.println("\nDELETE SITE\n");
        SiteController.validateNoTagsAndAttrs(action);
        String id = "";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) { id = p.getValue(); break; }
        }
            //verificar que no sea el index
        if (!files.pageIdExists(id)) throw new IllegalArgumentException("ID: "+id+" for pages not found");
        files.deletePage(id);
    }

    private void validateMODIFY(Action action) throws IllegalArgumentException {
        int title = 0, tags = 0;
        for (Param a: action.getParams() ) {
            switch (a.getType()){
                case ID -> {}
                case PARAM_TITLE -> title++;
                default -> throw new IllegalArgumentException("Attribute '" + a.getType().name() + "' not needed in the action");
            }
        }
        if ( title == 0 && action.getTags().isEmpty() ) throw new IllegalArgumentException("Action missing the 'TAGS' or parameter 'TITLE'");
        if ( title > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'TITLE' parameters");
        if (action.getAttributes() != null) throw new IllegalArgumentException("'ATTRIBUTES' not needed in the action");
    }
    private void validateNEW(Action action) throws IllegalArgumentException {
        int sites = 0, parent = 0, mod_date = 0, mod_us = 0, cr_us = 0, cr_date = 0, title = 0;
        for (Param p: action.getParams() ) {
            switch (p.getType()){
                case ID -> {}
                case SITE -> {
                    sites++;
                    ActionsController.validateReplaceId(p);
                }
                case PARENT -> {
                    parent++;
                    if (!p.getValue().equals("index")) ActionsController.validateReplaceId(p);
                }
                case MODIFICATION_DATE -> mod_date++;
                case MODIFICATION_USER -> mod_us++;
                case CREATION_DATE -> cr_date++;
                case CREATION_USER -> cr_us++;
                case PARAM_TITLE -> title++;
                default -> throw new IllegalArgumentException("Parameter '" + p.getType().name() + "' not needed in the action");
            }
        }
        if ( sites == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'SITE'");
        if ( parent == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'PARENT'");
        if ( sites > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'SITE' parameters");
        if ( parent > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'PARENT' parameters");

        if ( title > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'TITLE' parameters");
        if ( mod_date > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'MODIFICATION_DATE' parameters");
        if ( mod_us > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'MODIFICATION_USER' parameters");
        if ( cr_us > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'CREATION_USER' parameters");
        if ( cr_date > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'MODIFICATION_USER' parameters");
        if (action.getAttributes() != null) throw new IllegalArgumentException("'ATTRIBUTES' not needed in the action");
    }

    public PageController(FilesController files) {
        this.files = files;
    }
}
