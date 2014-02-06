/*
 * $Id: BasicInitializationState.java,v 1.1 2011/04/12 09:25:48 oeuillot Exp $
 */
package org.rcfaces.core.internal.component;

import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.listener.CameliaPhaseListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:48 $
 */
public class BasicInitializationState implements IInitializationState {

    private FacesContext facesContext;

    private Boolean isConstructionState;

    public boolean isConstructionState() {
        if (isConstructionState != null) {
            return isConstructionState.booleanValue();
        }

        FacesContext facesContext = getFacesContext();

        boolean ret = (CameliaPhaseListener.isRestoreViewPhase(facesContext) == false);

        isConstructionState = Boolean.valueOf(ret);

        return ret;
    }

    public FacesContext getFacesContext() {
        if (facesContext != null) {
            return facesContext;
        }

        facesContext = FacesContext.getCurrentInstance();
        return facesContext;
    }

}
