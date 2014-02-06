/*
 * $Id: IHeaderVisibilityCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IHeaderVisibilityCapability {

    /**
     * Returns a boolean value indicating wether the header should be visible.
     * 
     * @return true if the header is visible
     */
    boolean isHeaderVisible();

    /**
     * Sets a boolean value indicating wether the header should be visible.
     * 
     * @param headerVisible
     *            true if the header should be visible
     */
    void setHeaderVisible(boolean headerVisible);

}
