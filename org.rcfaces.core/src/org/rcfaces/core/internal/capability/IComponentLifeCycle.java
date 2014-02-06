/*
 * $Id: IComponentLifeCycle.java,v 1.2 2013/01/11 15:47:01 jbmeslin Exp $
 */
package org.rcfaces.core.internal.capability;

import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:01 $
 */
public interface IComponentLifeCycle {
    void constructPhase(FacesContext facesContext);
    
    void initializePhase(FacesContext facesContext, boolean reused);

    void settedPhase(FacesContext facesContext);

    void decodePhase(FacesContext facesContext);

    void validationPhase(FacesContext facesContext);

    void updatePhase(FacesContext facesContext);

    void renderPhase(FacesContext facesContext);
    
    boolean confirmListenerAppend(FacesContext facesContext,
            Class< ? extends FacesListener> facesListenerClass);
   
}
