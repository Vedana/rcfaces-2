/*
 * $Id: IInitializationState.java,v 1.1 2011/04/12 09:25:48 oeuillot Exp $
 */
package org.rcfaces.core.internal.component;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:48 $
 */
public interface IInitializationState {
    /**
     * Returns <code>true</code> if the component is just created.
     * 
     * @return <code>true</code> if this component is just created !
     */
    boolean isConstructionState();

    /**
     * Returns the faces context.
     */
    FacesContext getFacesContext();
}
