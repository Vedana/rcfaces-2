/*
 * $Id: IAutoTabCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value indicating if the focus should move automatically to the next
 * element when the entry is completed.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IAutoTabCapability {

    /**
     * Returns a boolean value indicating if the focus should move automatically
     * to the next element when the entry is completed.
     * 
     * @return boolean
     */
    boolean isAutoTab();

    /**
     * Sets a boolean value indicating if the focus should move automatically to
     * the next element when the entry is completed.
     * 
     * @param autoTab
     *            boolean
     */
    void setAutoTab(boolean autoTab);
}
