/*
 * $Id: UserEventListenerTag.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IUserEventCapability;
import org.rcfaces.core.event.IUserEventListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class UserEventListenerTag extends AbstractListenerTag {

    

    private static final long serialVersionUID = -3769010939257728457L;

    private static final Log LOG = LogFactory
            .getLog(UserEventListenerTag.class);

    protected void addListener(Object listener, UIComponent component) {
        addUserEventListener(listener, component);
    }

    public static void addUserEventListener(Object listener,
            UIComponent component) {

        if ((listener instanceof IUserEventListener) == false) {
            throw new FacesException("Listener '" + listener
                    + "' must implement IUserEventListener.");
        }

        IUserEventListener userEventListener = (IUserEventListener) listener;

        if ((component instanceof IUserEventCapability) == false) {
            LOG.error("Component '" + component.getId()
                    + "' does not implement IUserEventCapability.");
            return;
        }

        ((IUserEventCapability) component)
                .addUserEventListener(userEventListener);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Add userEventListener '" + userEventListener
                    + "' to component '" + component.getId() + "'.");
        }
    }

    protected String getListenerName() {
        return "userEvent";
    }

}
