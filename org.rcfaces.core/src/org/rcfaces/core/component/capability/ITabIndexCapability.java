/*
 * $Id: ITabIndexCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ITabIndexCapability {

    /**
     * Returns an int value specifying the position of this element in the
     * tabbing order for the current document. This value must be an integer
     * between 0 and 32767.
     * 
     * @return index
     */
    Integer getTabIndex();

    /**
     * Sets an int value specifying the position of this element in the tabbing
     * order for the current document. This value must be an integer between 0
     * and 32767.
     * 
     * @param tabIndex
     *            index
     */
    void setTabIndex(Integer tabIndex);
}
