/*
 * $Id: UserEvent.java,v 1.3 2013/11/13 12:53:20 jbmeslin Exp $
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
public class UserEvent extends ActionEvent {

    private static final long serialVersionUID = 6509010645116536466L;

    private final String value;

    private final int detail;

    private final String item;

    public UserEvent(UIComponent component, String value, String item,
            int detail) {
        super(component);

        this.value = value;
        this.item = item;
        this.detail = detail;
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof IUserEventListener);
    }

    @Override
    public void processListener(FacesListener listener) {
        ((IUserEventListener) listener).processUserEvent(this);
    }

    public final String getValue() {
        return value;
    }

    public int getDetail() {
        return detail;
    }

    public final String getItem() {
        return item;
    }

}
