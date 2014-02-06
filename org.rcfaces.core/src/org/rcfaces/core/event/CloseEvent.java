/*
 * $Id: CloseEvent.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class CloseEvent extends ActionEvent {

    private static final long serialVersionUID = 3236530136476420105L;

    private final String value;

    private final Object valueObject;

    public CloseEvent(UIComponent component, String value, Object valueObject) {
        super(component);

        this.value = value;
        this.valueObject = valueObject;
    }

    public String getValue() {
        return value;
    }

    public Object getValueObject() {
        return valueObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.event.FacesEvent#isAppropriateListener(javax.faces.event.
     * FacesListener)
     */
    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof ICloseListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.event.FacesEvent#processListener(javax.faces.event.FacesListener
     * )
     */
    @Override
    public void processListener(FacesListener listener) {
        ((ICloseListener) listener).processClose(this);
    }

}
