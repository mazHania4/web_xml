package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Param;

public class PageController {

    public void executeNEW(Action action) throws IllegalArgumentException {
        validateNEW(action);

    }

    public void executeMODIFY(Action action) throws IllegalArgumentException {
        validateMODIFY(action);

    }

    public void executeDELETE(Action action) throws IllegalArgumentException {

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
    }
    private void validateNEW(Action action) throws IllegalArgumentException {
        int sites = 0, parent = 0, mod_date = 0, mod_us = 0, cr_us = 0, cr_date = 0, title = 0;
        for (Param p: action.getParams() ) {
            switch (p.getType()){
                case ID -> {}
                case SITE -> sites++;
                case PARENT -> parent++;
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
    }

}
