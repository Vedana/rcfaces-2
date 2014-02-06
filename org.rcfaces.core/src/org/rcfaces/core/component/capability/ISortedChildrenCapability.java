/*
 * $Id: ISortedChildrenCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface ISortedChildrenCapability {

    /**
     * Set the ordered list of the sorted components
     */
    void setSortedChildren(UIComponent components[]);

    /**
     * Returns a string value giving the ordered list of the sorted components.
     * 
     * @return ordered list of the sorted components
     */
    UIComponent[] getSortedChildren();

}
