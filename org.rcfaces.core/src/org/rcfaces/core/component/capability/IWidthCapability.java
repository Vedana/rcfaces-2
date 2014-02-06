/*
 * $Id: IWidthCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IWidthCapability {

    /**
     * Returns a string value (as specified by CSS) for the width of the
     * component.
     * 
     * @return width
     */
    String getWidth();

    /**
     * Sets a string value (as specified by CSS) for the width of the component.
     * 
     * @param width
     *            width
     */
    void setWidth(String width);

}
