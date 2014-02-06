/*
 * $Id: IBorderTypeCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * A string that indicates the type of border the component should show.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IBorderTypeCapability {
    String NONE_BORDER_TYPE_NAME = "none";

    /**
     * Returns a string that indicates the type of border the component should
     * show.
     * 
     * @return none|solid|relief|flat|rounded
     */
    String getBorderType();

    /**
     * Sets a string that indicates the type of border the component should
     * show.
     * 
     * @param borderType
     *            none|solid|relief|flat|rounded
     */
    void setBorderType(String borderType);
}
