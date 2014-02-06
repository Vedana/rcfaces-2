/*
 * $Id: DoubleClickEvent.java,v 1.2 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:01 $
 */
public class DoubleClickEvent extends ActionEvent {
  

    private static final long serialVersionUID = 6193772389813084255L;

    private final String value;

    private final Object valueObject;

    private final Object item;

    public DoubleClickEvent(UIComponent component, String value,
            Object valueObject, Object item, int detail) {
        super(component);

        this.value = value;
        this.valueObject = valueObject;
        this.item = item;
    }

    public final String getValue() {
        return value;
    }

    public final Object getValueObject() {
        return valueObject;
    }

    public final Object getItem() {
        return item;
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
        return (listener instanceof IDoubleClickListener);
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
        ((IDoubleClickListener) listener).processDoubleClick(this);
    }

}
