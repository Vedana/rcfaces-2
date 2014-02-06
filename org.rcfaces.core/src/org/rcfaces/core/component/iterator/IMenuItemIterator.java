/*
 * $Id: IMenuItemIterator.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.iterator;

import org.rcfaces.core.component.MenuItemComponent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IMenuItemIterator extends
        IComponentIterator<MenuItemComponent> {

    MenuItemComponent next();

    MenuItemComponent[] toArray();
}
