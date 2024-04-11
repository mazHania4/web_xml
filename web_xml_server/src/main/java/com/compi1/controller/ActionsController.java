package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Param;
import com.compi1.model.actions.ParamType;

public class ActionsController {

    private final SiteController sites;
    private final PageController pages;
    private final CompController comps;

    public void execute(Action action) throws IllegalArgumentException {
        validateHasID(action);
        // each verifies that the necessary parameters/attributes are present
        // the extra parameters/attributes just will be ignored
        switch (action.getType()){
            case NEW_SITE -> sites.executeNEW(action);
            case DELETE_SITE -> sites.executeDELETE(action);
            case NEW_PAGE -> pages.executeNEW(action);
            case MODIFY_PAGE -> pages.executeMODIFY(action);
            case DELETE_PAGE -> pages.executeDELETE(action);
            case ADD_COMPONENT -> comps.executeADD(action);
            case MODIFY_COMPONENT -> comps.executeMODIFY(action);
            case DELETE_COMPONENT -> comps.executeDELETE(action);
        }
    }

    private void validateHasID(Action action) throws IllegalArgumentException{
        int ids = 0;
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) ids++;
        }
        if (ids == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'ID'");
        if (ids > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'ID' parameters");
    }

    public ActionsController() {
        sites = new SiteController();
        pages = new PageController();
        comps = new CompController();
    }
}
