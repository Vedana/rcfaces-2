/*
 * $Id: IMenuComponent.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component;

import org.rcfaces.core.component.capability.ICheckEventCapability;
import org.rcfaces.core.component.capability.ISelectionEventCapability;
import org.rcfaces.core.component.iterator.IMenuItemIterator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IMenuComponent extends ICheckEventCapability,
        ISelectionEventCapability {

    /*
     * int getItemImageWidth();
     * 
     * void setItemImageWidth(int width);
     * 
     * int getItemImageHeight();
     * 
     * void setItemImageHeight(int height);
     */
    IMenuItemIterator listMenuItems();

    boolean isRemoveAllWhenShown();

    void setRemoveAllWhenShown(boolean removeAllWhenShown);
}
