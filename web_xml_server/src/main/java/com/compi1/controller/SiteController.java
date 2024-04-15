package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Param;
import com.compi1.model.actions.ParamType;
import com.compi1.model.sites.Page;
import com.compi1.model.sites.Site;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class SiteController {
    private final FilesController files;

    public void executeNEW(Action action) throws RuntimeException {
        System.out.println("\nNEW SITE\n");
        validateNEW(action);
        String id = "", cr_user="", cr_s_date="", mod_user="", mod_s_date="";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) id = p.getValue();
            if (p.getType().equals(ParamType.CREATION_USER)) cr_user = p.getValue();
            if (p.getType().equals(ParamType.CREATION_DATE)) cr_s_date = p.getValue();
            if (p.getType().equals(ParamType.MODIFICATION_USER)) mod_user = p.getValue();
            if (p.getType().equals(ParamType.MODIFICATION_DATE)) mod_s_date = p.getValue();
        }
        if (files.siteIdExists(id)) throw new IllegalArgumentException("ID: "+id+" for sites already exists");
        if (cr_user.isEmpty()) cr_user = "-unknown-";
        if (mod_user.isEmpty()) cr_user = "-unknown-";
        LocalDate cr_date = processDate(cr_s_date);
        LocalDate mod_date = processDate(mod_s_date);
        Site site = Site.builder()
                .id(id)
                .cr_user(cr_user)
                .cr_date(cr_date)
                .mod_user(mod_user)
                .mod_date(mod_date)
                .page_visits(new HashMap<>())
                .build();
        Page index = Page.builder()
                .parentId("-")
                .id(id+"_index")
                .title("index")
                .components(new ArrayList<>())
                .subPageIds(new ArrayList<>())
                .tags(new ArrayList<>())
                .cr_user(cr_user)
                .mod_user(mod_user)
                .cr_date(cr_date)
                .mod_date(mod_date)
                .build();
        files.addSite(site, index);
    }

    public void executeDELETE(Action action) throws RuntimeException {
        System.out.println("\nDELETE SITE\n");
        validateNoTagsAndAttrs(action);
        String id = "";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) { id = p.getValue(); break; }
        }
        if (!files.siteIdExists(id)) throw new IllegalArgumentException("ID: "+id+" for sites not found");
        files.deleteSite(id);
    }

    private void validateNEW(Action action) throws IllegalArgumentException {
        validateNoTagsAndAttrs(action);
        int mod_date = 0, mod_us = 0, cr_us = 0, cr_date = 0;
        for (Param p: action.getParams() ) {
            switch (p.getType()){
                case ID -> {}
                case MODIFICATION_DATE -> mod_date++;
                case MODIFICATION_USER -> mod_us++;
                case CREATION_DATE -> cr_date++;
                case CREATION_USER -> cr_us++;
                default -> throw new IllegalArgumentException("Parameter '" + p.getType().name() + "' not needed in the action");
            }
        }
        if ( mod_date > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'MODIFICATION_DATE' parameters");
        if ( mod_us > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'MODIFICATION_USER' parameters");
        if ( cr_us > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'CREATION_USER' parameters");
        if ( cr_date > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'MODIFICATION_USER' parameters");
    }

    public static LocalDate processDate(String date){
        if (!date.isEmpty()){
            try {
                return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Wrong value for DATE:'"+date+"'");
            }
        } else return LocalDate.now();
    }
    public static void validateNoTagsAndAttrs(Action action) {
        if (action.getTags() != null) throw new IllegalArgumentException("'TAGS' not needed in the action");
        if (action.getAttributes() != null) throw new IllegalArgumentException("'ATTRIBUTES' not needed in the action");
    }

    public SiteController(FilesController files) {
        this.files = files;
    }
}
