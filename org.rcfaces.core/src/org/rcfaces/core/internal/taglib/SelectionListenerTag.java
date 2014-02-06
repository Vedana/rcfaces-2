/*
 * $Id: SelectionListenerTag.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ISelectionEventCapability;
import org.rcfaces.core.event.ISelectionListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class SelectionListenerTag extends AbstractListenerTag {
    

    private static final long serialVersionUID = 5117953058731866439L;

    private static final Log LOG = LogFactory
            .getLog(SelectionListenerTag.class);

    protected void addListener(Object type, UIComponent component) {
        addSelectionListener(type, component);
    }

    public static void addSelectionListener(Object listener,
            UIComponent component) {
        if ((listener instanceof ISelectionListener) == false) {
            throw new FacesException("Listener '" + listener
                    + "' must implement ISelectionListener.");
        }

        ISelectionListener selectionListener = (ISelectionListener) listener;

        if ((component instanceof ISelectionEventCapability) == false) {
            LOG.error("Component '" + component.getId()
                    + "' does not implement ISelectionEventCapability.");
            return;
        }

        ((ISelectionEventCapability) component)
                .addSelectionListener(selectionListener);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Add selectionListener '" + selectionListener
                    + "' to component '" + component.getId() + "'.");
        }
    }

    protected String getListenerName() {
        return "selection";
    }

}
