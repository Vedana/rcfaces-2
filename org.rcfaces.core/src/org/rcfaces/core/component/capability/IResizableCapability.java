/*
 * $Id: IResizableCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IResizableCapability {

    /**
     * Returns a boolean value indicating wether the component can be resized by
     * the user.
     * 
     * @return resizeable boolean property
     */
    boolean isResizable();

    /**
     * Sets a boolean value indicating wether the component can be resized by
     * the user.
     * 
     * @param resizable
     *            resizeable boolean property
     */
    void setResizable(boolean resizable);
}
