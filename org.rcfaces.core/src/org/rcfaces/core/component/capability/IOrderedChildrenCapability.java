/*
 * $Id: IOrderedChildrenCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IOrderedChildrenCapability {
    /**
     * Returns a list of the columns' id which represents their order from left
     * to right.
     * 
     * @return ordered list of columns' id
     */
    UIComponent[] getOrderedChildren();

    /**
     * Sets a list of the columns' id which represents their order from left to
     * right.
     * 
     * @param children
     *            ordered list of columns' id
     */
    void setOrderedChildren(UIComponent children[]);

}
