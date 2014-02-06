/*
 * $Id: CameliaEvent.java,v 1.2 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

import org.rcfaces.core.internal.listener.IScriptListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:01 $
 */
abstract class CameliaEvent extends FacesEvent implements ITypedEvent {
    private static final long serialVersionUID = -2844437297380361681L;

    protected final int type;

    public CameliaEvent(UIComponent component) {
        this(component, 0);
    }

    public CameliaEvent(UIComponent component, int type) {
        super(component);

        this.type = type;
    }

    public final int getType() {
        return type;
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        if (listener instanceof IScriptListener) {
            return false;
        }

        return true;
    }

}
