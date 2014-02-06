/*
 * $Id: IMenuCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import org.rcfaces.core.component.IMenuComponent;
import org.rcfaces.core.component.iterator.IMenuIterator;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IMenuCapability {

    /**
     * Returns the top-most menu associated to the component.
     * 
     * @return top-most menu component
     */
    IMenuComponent getMenu();

    /**
     * Returns the menu identified by its id from the menus associated to the
     * component.
     * 
     * @param menuId
     *            Id for a particular menu
     * @return menu component
     */
    IMenuComponent getMenu(String menuId);

    /**
     * Returns a list of all the menus associated to the component.
     * 
     * @return Menu iterator
     */
    IMenuIterator listMenus();
}
