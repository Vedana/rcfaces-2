/*
 * $Id: IMouseEventCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IMouseOutListener;
import org.rcfaces.core.event.IMouseOverListener;

/**
 * 
 * GIves mouseOver and mouseOut event managements capabilities
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IMouseEventCapability {

    /**
     * Adds a listener to the component for the mouseOver event
     * 
     * @param facesListener
     *            the mouseOver listener to add
     */
    void addMouseOverListener(IMouseOverListener facesListener);

    /**
     * Removes a listener from the component for the mouseOver event
     * 
     * @param facesListener
     *            the mouseOver listener to remove
     */
    void removeMouseOverListener(IMouseOverListener facesListener);

    /**
     * Returns a list of mouseOver listener for the component
     * 
     * @return mouseOver listeners' list
     */
    FacesListener[] listMouseOverListeners();

    /**
     * Adds a listener to the component for the mouseOut event
     * 
     * @param facesListener
     *            the mouseOut listener to add
     */
    void addMouseOutListener(IMouseOutListener facesListener);

    /**
     * Removes a listener from the component for the mouseOut event
     * 
     * @param facesListener
     *            the mouseOut listener to remove
     */
    void removeMouseOutListener(IMouseOutListener facesListener);

    /**
     * Returns a list of mouseOut listener for the component
     * 
     * @return mouseOut listeners' list
     */
    FacesListener[] listMouseOutListeners();

}
