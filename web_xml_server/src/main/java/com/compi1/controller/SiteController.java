package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Param;

public class SiteController {


    public void executeNEW(Action action) throws IllegalArgumentException {
        validateNEW(action);
        
    }

    public void executeDELETE(Action action) throws IllegalArgumentException {

    }

    private void validateNEW(Action action) throws IllegalArgumentException {
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

}
