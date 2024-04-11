package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Attr;
import com.compi1.model.actions.Param;
import com.compi1.model.actions.ParamType;

import java.util.List;

public class CompController {

    public void executeADD(Action action) throws IllegalArgumentException {
        validateADD_MODIFY(action);
        //verifies if the page id exists (possibly uses the pageController)
        //tries to get the page from the serialized
        //verifies if the page has a component with the id

    }

    public void executeMODIFY(Action action) throws IllegalArgumentException {
        validateADD_MODIFY(action);
        //verifies if the page id exists (possibly uses the pageController)
        //tries to get the page from the serialized
        //verifies if the page has a component with the id

    }

    public void executeDELETE(Action action) throws IllegalArgumentException {
        validateDELETE(action);
        //verifies if the page id exists (possibly uses the pageController)
        //tries to get the page from the serialized
        //verifies if the page has a component with the id
    }


    private void validateADD_MODIFY(Action action) throws IllegalArgumentException {
        int pages = 0;
        int classes = 0;
        String className = "";
        for (Param p: action.getParams() ) {
            switch (p.getType()){
                case PARAM_PAGE -> pages++;
                case CLASS ->  {
                    classes++;
                    className = p.getValue();
                }
                case ID -> {}
                default -> throw new IllegalArgumentException("Parameter '" + p.getType().name() + "' not needed in the action");
            }
        }
        if ( pages == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'PAGE'");
        if ( classes == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'CLASS'");
        if (pages > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'PAGE' parameters");
        if (classes > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'CLASS' parameters");
        if (action.getAttributes() == null) throw new IllegalArgumentException("Action missing attributes");
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
                case PARENT -> parents++;
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
                case ALIGNMENT -> align++;
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
            if (p.getType().equals(ParamType.PARAM_PAGE)) pages++;
            if (!p.getType().equals(ParamType.PARAM_PAGE) && !p.getType().equals(ParamType.ID)) throw new IllegalArgumentException("Parameter '" + p.getType().name() + "' not needed in the action");
        }
        if (pages == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'PAGE'");
        if (pages > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'PAGE' parameters");
    }

}
