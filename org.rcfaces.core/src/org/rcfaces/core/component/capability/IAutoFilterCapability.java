/*
 * $Id: IAutoFilterCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IAutoFilterCapability {

    /**
     * Returns a boolean value indicating if the component should apply filter
     * automatically.
     * 
     * @return true if the component should apply filter
     */
    boolean isAutoFilter();

    /**
     * Sets a boolean value indicating if the component should apply filter
     * automatically.
     * 
     * @param autoFilter
     *            true if the component should apply filter
     */
    void setAutoFilter(boolean autoFilter);
}
