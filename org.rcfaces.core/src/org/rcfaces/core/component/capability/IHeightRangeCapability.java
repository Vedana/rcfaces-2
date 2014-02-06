/*
 * $Id: IHeightRangeCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IHeightRangeCapability extends IHeightCapability {

    /**
     * Returns an int value specifying the maximum height in pixels (if
     * resizeable).
     * 
     * @return max height
     */
    int getMaxHeight();

    /**
     * Sets an int value specifying the maximum height in pixels (if resizeable).
     * 
     * @param maxHeight
     *            max height
     */
    void setMaxHeight(int maxHeight);

    /**
     * Returns an int value specifying the minimum height in pixels (if
     * resizeable).
     * 
     * @return min height
     */
    int getMinHeight();

    /**
     * Sets an int value specifying the minimum height in pixels (if resizeable).
     * 
     * @param minHeight
     *            min height
     */
    void setMinHeight(int minHeight);
}
