/*
 * $Id: IHeightCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IHeightCapability {

    /**
     * Returns a string value (as specified by CSS) for the height of the
     * component.
     * 
     * @return height
     */
    String getHeight();

    /**
     * Sets a string value (as specified by CSS) for the height of the
     * component.
     * 
     * @param height
     *            height
     */
    void setHeight(String height);
}
