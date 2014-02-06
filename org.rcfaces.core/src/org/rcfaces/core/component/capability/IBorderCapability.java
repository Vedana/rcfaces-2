/*
 * $Id: IBorderCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value that indicates if the component should show a border or not.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IBorderCapability {

    /**
     * Returns a boolean value that indicates if the component should show a
     * border or not.
     * 
     * @return border
     */
    boolean isBorder();

    /**
     * Sets a boolean value that indicates if the component should show a border
     * or not.
     * 
     * @param border
     *            boolean
     */
    void setBorder(boolean border);
}
