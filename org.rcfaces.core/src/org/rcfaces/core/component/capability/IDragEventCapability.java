/*
 * $Id: IDragEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IDragListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IDragEventCapability {

    /**
     * Adds a listener to the component for the drag event
     * 
     * @param facesListener
     *            the drag listener to add
     */
    void addDragListener(IDragListener facesListener);

    /**
     * Removes a listener from the component for the drag event
     * 
     * @param facesListener
     *            the drag listener to remove
     */
    void removeDragListener(IDragListener facesListener);

    /**
     * Returns a list of drag listener for the component
     * 
     * @return drag listeners' list
     */
    FacesListener[] listDragListeners();
}