package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.actions.Param;
import com.compi1.model.actions.ParamType;

import java.io.*;

public class ActionsController {

    private final SiteController sites;
    private final PageController pages;
    private final CompController comps;

    public void execute(Action action) throws IllegalArgumentException {
        validateHasID(action);
        // each verifies that the necessary parameters/attributes are present
        // and there are no extra parameters/attributes/tags unnecessary
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
        //verify id regex, id can't be 'site' or other files names saved in folder of a site
        int ids = 0;
        for (Param p: action.getParams() ) {
            if (p.getType().equals(ParamType.ID)) ids++;
        }
        if (ids == 0 ) throw new IllegalArgumentException("Action missing the parameter: 'ID'");
        if (ids > 1 ) throw new IllegalArgumentException("Action cannot have multiple 'ID' parameters");
    }

    public ActionsController() {
        FilesController files;
        try {
            FileInputStream fileInputStream = new FileInputStream(FilesController.rootFolder+"ids.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            files = (FilesController) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(" - Couldn't retrieve saved list of Ids, generating new empty lists - ");
            files = new FilesController();
        }
        sites = new SiteController(files);
        pages = new PageController(files);
        comps = new CompController(files);
    }
}