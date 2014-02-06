/*
 * $Id: PropertyChangeEvent.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.event;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesListener;
import javax.faces.event.ValueChangeEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class PropertyChangeEvent extends ValueChangeEvent {

    

    private static final long serialVersionUID = -4109774105032276791L;

    private final String propertyName;

    public PropertyChangeEvent(UIComponent component, Serializable propertyName,
            Object oldValue, Object newValue) {
        this(component, String.valueOf(propertyName), oldValue, newValue);
    }

    @Deprecated
    public PropertyChangeEvent(UIComponent component, String propertyName,
            Object oldValue, Object newValue) {
        super(component, oldValue, newValue);

        this.propertyName = propertyName;
    }

    public final String getPropertyName() {
        return propertyName;
    }

    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof IPropertyChangeListener);
    }

    public void processListener(FacesListener listener) {
        ((IPropertyChangeListener) listener).processPropertyChange(this);
    }

}