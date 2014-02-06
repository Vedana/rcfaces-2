/*
 * $Id: IOrientationCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 *
 */
package org.rcfaces.core.component.capability;

/**
 * A string value specifying the orientation of the component :
 * <ul>
 * <li> horizontal (default value) </li>
 * <li> vertical </li>
 * </ul>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IOrientationCapability {

    /**
     * Returns a string value specifying the orientation of the component.
     * 
     * @return horizontal|vertical
     */
    String getOrientation();

    /**
     * Sets a string value specifying the orientation of the component.
     * 
     * @param orientation
     *            horizontal|vertical
     */
    void setOrientation(String orientation);

}
