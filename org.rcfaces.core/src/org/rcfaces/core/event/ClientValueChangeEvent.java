/*
 * $Id: ClientValueChangeEvent.java,v 1.1 2011/04/12 09:25:15 oeuillot Exp $
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeEvent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:15 $
 */
public class ClientValueChangeEvent extends ValueChangeEvent {

    private static final long serialVersionUID = 3196578500483059239L;

    public ClientValueChangeEvent(UIComponent component, Object oldValue,
            Object newValue) {

        super(component, oldValue, newValue);
    }

}
