/*
 * $Id: CheckListenerTag.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ICheckEventCapability;
import org.rcfaces.core.event.ICheckListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class CheckListenerTag extends AbstractListenerTag {

    

    private static final long serialVersionUID = 4827027133242453987L;

    private static final Log LOG = LogFactory.getLog(CheckListenerTag.class);

    protected void addListener(Object type, UIComponent component) {
        addCheckListener(type, component);
    }

    public static void addCheckListener(Object listener, UIComponent component)
            throws FacesException {
        if ((listener instanceof ICheckListener) == false) {
            throw new FacesException("Listener '" + listener
                    + "' must implement ICheckListener.");
        }

        ICheckListener checkListener = (ICheckListener) listener;

        if ((component instanceof ICheckEventCapability) == false) {
            LOG.error("Component '" + component.getId()
                    + "' does not implement ICheckEventCapability.");
            return;
        }

        ((ICheckEventCapability) component).addCheckListener(checkListener);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Add checkListener '" + checkListener
                    + "' to component '" + component.getId() + "'.");
        }
    }

    protected String getListenerName() {
        return "check";
    }

}
