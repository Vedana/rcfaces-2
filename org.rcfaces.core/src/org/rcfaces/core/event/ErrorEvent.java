/*
 * $Id: ErrorEvent.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:20 $
 */
public class ErrorEvent extends ActionEvent {

    private static final long serialVersionUID = -1530737844134123632L;

    public ErrorEvent(UIComponent component) {
        super(component);
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof IErrorListener);
    }

    @Override
    public void processListener(FacesListener listener) {
        ((IErrorListener) listener).processError(this);
    }

}
