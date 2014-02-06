/*
 * $Id: IForegroundBackgroundColorCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * Foreground et background colors.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IForegroundBackgroundColorCapability {

    /**
     * Returns the receiver's background color.
     * 
     * @return the background color
     */
    String getBackgroundColor();

    /**
     * Sets the receiver's background color to the color specified by the
     * argument, or to the default system color for the control if the argument
     * is null.
     * 
     * @param color
     *            background color
     */
    void setBackgroundColor(String color);

    /**
     * Returns the foreground color that the receiver will use to draw.
     * 
     * @return the receiver's foreground color
     */
    String getForegroundColor();

    /**
     * Sets the receiver's foreground color to the color specified by the
     * argument, or to the default system color for the control if the argument
     * is null.
     * 
     * @param color
     *            foreground color
     */
    void setForegroundColor(String color);
}
