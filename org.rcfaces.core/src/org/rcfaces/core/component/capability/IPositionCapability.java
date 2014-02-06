/*
 * $Id: IPositionCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IPositionCapability {

    /**
     * Returns a string value (as specified by CSS) for the x position of the
     * component.
     * 
     * @return as specified by CSS
     */
    String getX();

    /**
     * Sets a string value (as specified by CSS) for the x position of the
     * component.
     * 
     * @param x
     *            as specified by CSS
     */
    void setX(String x);

    /**
     * Returns a string value (as specified by CSS) for the y position of the
     * component.
     * 
     * @return as specified by CSS
     */
    String getY();

    /**
     * Sets a string value (as specified by CSS) for the y position of the
     * component.
     * 
     * @param y
     *            as specified by CSS
     */
    void setY(String y);
}
