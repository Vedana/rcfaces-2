/*
 * $Id: ICollapsableCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value indicating wether the component is collapsed.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ICollapsableCapability {

    /**
     * Returns a boolean value indicating wether the component is collapsed.
     * 
     * @return boolean
     */
    boolean isCollapsed();

    /**
     * Sets a boolean value indicating wether the component should be collapsed.
     * 
     * @param collapsed
     *            boolean
     */
    void setCollapsed(boolean collapsed);
}
