package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Attr;
import com.compi1.model.actions.Param;
import com.compi1.model.actions.ParamType;
import com.compi1.model.sites.Alignment;
import com.compi1.model.sites.Component;
import com.compi1.model.sites.ComponentType;
import com.compi1.model.sites.Page;

import java.util.ArrayList;
import java.util.List;

public class CompController {

    private final FilesController files;


    public void executeADD(Action action) throws IllegalArgumentException {
        validateADD_MODIFY(action);
        String cId = "", pId  = "", classN = "";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID))  cId = p.getValue();
            if (p.getType().equals(ParamType.PARAM_PAGE))  pId = p.getValue();
            if (p.getType().equals(ParamType.CLASS))  classN = p.getValue();
        }
        if (!files.pageIdExists(pId)) throw new IllegalArgumentException("ID: "+pId+" for pages not found");
        Page page = files.getPage(pId);
        List<Component> comps = page.getComponents();
        for (Component c : comps){
            if (c.getId().equals(cId)) throw new IllegalArgumentException("ID: "+cId+" for components already exists within the page");
        }
        Component component = createComponent(action, cId, classN);
        page.getComponents().add(component);
        files.rewritePage(page);
    }

    public void executeMODIFY(Action action) throws IllegalArgumentException {
        validateADD_MODIFY(action);
        String cId = "", pId  = "", classN = "";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID))  cId = p.getValue();
            if (p.getType().equals(ParamType.PARAM_PAGE))  pId = p.getValue();
            if (p.getType().equals(ParamType.CLASS))  classN = p.getValue();
        }
        if (!files.pageIdExists(pId)) throw new IllegalArgumentException("ID: "+pId+" for pages not found");
        Page page = files.getPage(pId);
        List<Component> comps = page.getComponents();
        int i = validateGetCompI(comps, cId);
        Component component = createComponent(action, cId, classN);
        comps.remove(i);
        comps.add(i, component);
        files.rewritePage(page);
    }

    public void executeDELETE(Action action) throws IllegalArgumentException {
        validateDELETE(action);
        String cId = "", pId  = "";
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID))  cId = p.getValue();
            if (p.getType().equals(ParamType.PARAM_PAGE))  pId = p.getValue();
        }
        if (!files.pageIdExists(pId)) throw new IllegalArgumentException("ID: "+pId+" for pages not found");
        Page page = files.getPage(pId);
        List<Component> comps = page.getComponents();
        int i = validateGetCompI(comps, cId);
        comps.remove(i);
        files.rewritePage(page);
    }

    private Component createComponent(Action action, String id, String className){
        ComponentType type = null;
        switch (className){
            case "TITULO" -> type = ComponentType.TITLE;
            case "PARRAFO" -> type = ComponentType.PARAGRAPH;
            case "IMAGEN"-> type = ComponentType.IMG;
            case "VIDEO" -> type = ComponentType.VIDEO;
            case "MENU" -> type = ComponentType.MENU;
        }
        String text = "-", color = "#000000", align = "", src = "-", parent = "-", heightS = "", widthS = "", tagsS = "";
        for (Attr a: action.getAttributes() ) {
            switch (a.getType()){
                case TEXT -> text = a.getValue();
                case COLOR -> color = a.getValue();
                case ALIGNMENT -> align = a.getValue();
                case SOURCE -> src = a.getValue();
                case PARENT -> parent = a.getValue();
                case HEIGHT -> heightS = a.getValue();
                case WIDTH -> widthS = a.getValue();
                case COMP_TAGS -> tagsS = a.getValue();
            }
        }
        Alignment alignment = null;
        if (!align.isEmpty()) {
            switch (align) {
                case "IZQUIERDA" -> alignment = Alignment.LEFT;
                case "DERECHA" -> alignment = Alignment.RIGHT;
                case "CENTRAR" -> alignment = Alignment.CENTER;
                case "JUSTIFICAR" -> alignment = Alignment.JUSTIFY;
            }
        }
        int height = 0, width = 0;
        if (!heightS.isEmpty() || !widthS.isEmpty()) {
            try {
                height = Integer.parseInt(heightS);
                width = Integer.parseInt(widthS);
            } catch (NumberFormatException e){
                throw new RuntimeException("Wrong value for dimension:"+e.getMessage());
            }
        }
        List<String> tags = new ArrayList<>();
        if (!tagsS.isEmpty()) tags = List.of(tagsS.split("\\|"));
        if (!color.matches("#([0-9a-fA-F]{6})")) throw new RuntimeException("Wrong value for color: '"+color + "' must be a hex value");
        return Component.builder()
                .id(id)
                .type(type)
                .text(text)
                .alignment(alignment)
                .color(color)
                .src(src)
                .height(height)
                .width(width)
                .parent(parent)
                .tags(tags)
                .build();
    }

    private int validateGetCompI(List<Component> comps, String id){
        int i = -1;
        for (int j = 0; j<comps.size(); j++){
            if (comps.get(j).getId().equals(id)) { i = j; break;}
        }
        if (i == -1) throw new IllegalArgumentException("ID: "+id+" not found within the page");
        return i;
    }

    private void validateADD_MODIFY(Action action) throws IllegalArgumentException {
        int pages = 0;
        int classes = 0;
        String className = "";
        for (Param p: action.getParams() ) {
            switch (p.getType()){
                case PARAM_PAGE -> {
                    pages++;
                    ActionsController.validateRegexId(p.getValue());
                }
                case CLASS ->  {
                    classes++;
                    className = p.getValue();
                }
                case ID -> {
                    ActionsController.validateRegexId(p.getValue());
                    p.setValue(replaceId(p.getValue()));
                }
                default -> throw new IllegalArgumentException("Parameter '" + p.getType().name() + "' not needed in the action");
            }
        }
        if ( pages == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'PAGE'");
        if ( classes == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'CLASS'");
        if (pages > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'PAGE' parameters");
        if (classes > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'CLASS' parameters");
        if (!action.getTags().isEmpty()) throw new IllegalArgumentException("'TAGS' not needed in the action");
        if (action.getAttributes().isEmpty()) throw new IllegalArgumentException("Action missing attributes");
        switch (className){
            case "TITULO", "PARRAFO" -> validateTITLE_PARRAGRAPH(action.getAttributes());
            case "IMAGEN"-> validateIMG(action.getAttributes());
            case "VIDEO" -> validateVIDEO(action.getAttributes());
            case "MENU" -> validateMENU(action.getAttributes());
            default -> throw new IllegalArgumentException("Invalid value for 'CLASS' parameter");
        }
    }

    private void validateMENU(List<Attr> attrs) throws IllegalArgumentException {
        int parents = 0, tags = 0;
        for (Attr a: attrs ) {
            switch (a.getType()){
                case PARENT -> {
                    parents++;
                    ActionsController.validateRegexId(a.getValue());
                }
                case COMP_TAGS -> tags++;
                default -> throw new IllegalArgumentException("Attribute '" + a.getType().name() + "' not needed in the action");
            }
        }
        if ( parents == 0 && tags == 0) throw new IllegalArgumentException("Action missing the attribute: 'PARENT' or 'TAGS'");
        if (parents > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'PARENT' attributes");
        if (tags > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'TAGS' attributes");

    }

    //PARENT or TAGS *

    private void validateVIDEO(List<Attr> attrs) throws IllegalArgumentException {
        int src = 0, height = 0, width = 0;
        for (Attr a: attrs) {
            switch (a.getType()){
                case SOURCE -> src++;
                case HEIGHT -> height++;
                case WIDTH -> width++;
                default -> throw new IllegalArgumentException("Attribute '" + a.getType().name() + "' not needed in the action");
            }
        }
        validateIMG_VIDEO(src, height, width);
    }

    private void validateIMG_VIDEO(int src, int height, int width) throws IllegalArgumentException {
        if (src == 0 ) throw new IllegalArgumentException("Action missing the attribute: 'SOURCE'");
        if (src > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'SOURCE' attributes");
        if (height == 0 ) throw new IllegalArgumentException("Action missing the attribute: 'HEIGHT'");
        if (height > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'HEIGHT' attributes");
        if (width == 0 ) throw new IllegalArgumentException("Action missing the attribute: 'WIDTH'");
        if (width > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'WIDTH' attributes");
    }

    private void validateIMG(List<Attr> attrs) throws IllegalArgumentException {
        int src = 0, height = 0, width = 0, align = 0;
        for (Attr a: attrs ) {
            switch (a.getType()){
                case SOURCE -> src++;
                case HEIGHT -> height++;
                case WIDTH -> width++;
                case ALIGNMENT -> { align++;
                    if ( !( a.getValue().equals("CENTRAR") || a.getValue().equals("IZQUIERDA") ||
                            a.getValue().equals("DERECHA") || a.getValue().equals("JUSTIFICAR"))
                    ) throw new IllegalArgumentException("Invalid value for 'ALIGNMENT' attribute");
                }
                default -> throw new IllegalArgumentException("Attribute '" + a.getType().name() + "' not needed in the action");
            }
        }
        if (align > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'ALIGNMENT' attributes");
        validateIMG_VIDEO(src, height, width);
    }

    private void validateTITLE_PARRAGRAPH(List<Attr> attrs) throws IllegalArgumentException {
        int texts = 0, color = 0, align = 0;
        for (Attr a: attrs ) {
            switch (a.getType()){
                case TEXT -> texts++;
                case COLOR -> color++;
                case ALIGNMENT -> align++;
                default -> throw new IllegalArgumentException("Attribute '" + a.getType().name() + "' not needed in the action");
            }
        }
        if (texts == 0 ) throw new IllegalArgumentException("Action missing the attribute: 'TEXT'");
        if (texts > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'TEXT' attributes");
        if (color > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'COLOR' attributes");
        if (align > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'ALIGNMENT' attributes");
    }

    private void validateDELETE(Action action) throws IllegalArgumentException {
        int pages = 0;
        for (Param p: action.getParams() ) {
            switch (p.getType()){
                case ID -> {
                    ActionsController.validateRegexId(p.getValue());
                    p.setValue(replaceId(p.getValue()));
                }
                case PARAM_PAGE -> {
                    pages++;
                    ActionsController.validateRegexId(p.getValue());
                }
                default -> throw new IllegalArgumentException("Parameter '" + p.getType().name() + "' not needed in the action");
            }
        }
        if (pages == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'PAGE'");
        if (pages > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'PAGE' parameters");
        SiteController.validateNoTagsAndAttrs(action);
    }

    private String replaceId(String id){
        return id.replace("$", "S").replace("_", "L").replace("-", "H");
    }

    public CompController(FilesController files) {
        this.files = files;
    }
}
