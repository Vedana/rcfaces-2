/*
 * $Id: CriteriaEvent.java,v 1.2 2013/11/13 12:53:20 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:20 $
 */
public class CriteriaEvent extends ActionEvent {

    private static final long serialVersionUID = -8778872157368114361L;

    public CriteriaEvent(UIComponent component) {
        super(component);
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {

        if (listener instanceof ICriteriaListener) {
            return true;
        }

        return super.isAppropriateListener(listener);
    }

    @Override
    public void processListener(FacesListener listener) {
        if (listener instanceof ICriteriaListener) {
            ((ICriteriaListener) listener).processCriteriaChanged(this);
            return;
        }

        super.processListener(listener);
    }

}
