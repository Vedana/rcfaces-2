/*
 * $Id: MenuTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.rcfaces.core.component.AbstractMenuComponent;
import org.rcfaces.core.component.IMenuComponent;
import org.rcfaces.core.component.MenuComponent;
import org.rcfaces.core.component.MenuItemComponent;
import org.rcfaces.core.component.iterator.IMenuItemIterator;
import org.rcfaces.core.component.iterator.IMenuIterator;
import org.rcfaces.core.internal.manager.IContainerManager;
import org.rcfaces.core.internal.tools.CollectionTools.IComponentValueType;
import org.rcfaces.core.internal.util.ComponentIterators;
import org.rcfaces.core.lang.OrderedSet;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class MenuTools {

    private static final IMenuItemIterator EMPTY_MENU_ITEM_ITERATOR = new MenuItemListIterator(
            Collections.<MenuItemComponent> emptyList());

    private static final IMenuIterator EMPTY_MENU_ITERATOR = new MenuListIterator(
            Collections.<MenuComponent> emptyList());

    private static final String MENU_FACET = "popup";

    public static final IMenuItemIterator listMenuItems(
            AbstractMenuComponent menu) {
        return listMenuItems((IContainerManager) menu);
    }

    public static final IMenuItemIterator listMenuItems(
            MenuItemComponent menuItem) {
        return listMenuItems((IContainerManager) menuItem);
    }

    public static final IMenuItemIterator listMenuItems(MenuComponent menu) {
        return listMenuItems((IContainerManager) menu);
    }

    private static IMenuItemIterator listMenuItems(IContainerManager manager) {

        List<MenuItemComponent> list = ComponentIterators.list(
                (UIComponent) manager, MenuItemComponent.class);
        if (list.isEmpty()) {
            return EMPTY_MENU_ITEM_ITERATOR;
        }

        return new MenuItemListIterator(list);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class MenuItemListIterator extends
            ComponentIterators.ComponentListIterator<MenuItemComponent>
            implements IMenuItemIterator {

        public MenuItemListIterator(List<MenuItemComponent> list) {
            super(list);
        }

        public final MenuItemComponent next() {
            return nextComponent();
        }

        public MenuItemComponent[] toArray() {
            return (MenuItemComponent[]) toArray(new MenuItemComponent[count()]);
        }
    }

    public static IMenuComponent getMenu(MenuItemComponent item) {
        for (;;) {
            UIComponent component = item.getParent();
            if (component == null) {
                return null;
            }

            if (component instanceof MenuComponent) {
                return (MenuComponent) component;
            }

            if ((component instanceof MenuItemComponent) == false) {
                return null;
            }

            item = (MenuItemComponent) component;
        }
    }

    public static IMenuComponent getMenu(UIComponent component) {
        return searchMenu(component, null);
    }

    public static IMenuComponent getMenu(UIComponent component, String menuId) {
        return searchMenu(component, menuId);
    }

    private static IMenuComponent searchMenu(UIComponent container,
            String menuId) {
        List<UIComponent> children = container.getChildren();
        if (children.isEmpty()) {
            return null;
        }

        for (Iterator<UIComponent> it = children.iterator(); it.hasNext();) {
            UIComponent child = it.next();

            if ((child instanceof IMenuComponent) == false) {
                continue;
            }
            if (menuId != null && (child instanceof MenuComponent)) {
                MenuComponent menuComponent = (MenuComponent) child;

                if (menuId.equals(menuComponent.getMenuId()) == false) {
                    continue;
                }
            }

            return (IMenuComponent) child;
        }

        return null;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    private static final class MenuListIterator extends
            ComponentIterators.ComponentListIterator<MenuComponent> implements
            IMenuIterator {

        public MenuListIterator(List<MenuComponent> list) {
            super(list);
        }

        public final MenuComponent next() {
            return nextComponent();
        }

        public MenuComponent[] toArray() {
            return (MenuComponent[]) toArray(new MenuComponent[count()]);
        }
    }

    public static IMenuIterator listMenus(IContainerManager component) {
        Map<String, UIComponent> facets = ((UIComponent) component).getFacets();
        if (facets != null && facets.isEmpty() == false) {
            List<MenuComponent> popups = null;
            int i = 0;
            for (Iterator<UIComponent> it = facets.values().iterator(); it
                    .hasNext();) {
                UIComponent child = it.next();

                if ((child instanceof MenuComponent) == false) {
                    continue;
                }

                if (popups == null) {
                    popups = new ArrayList<MenuComponent>(facets.size() - i);
                }
                popups.add((MenuComponent) child);
            }

            if (popups == null) {
                return EMPTY_MENU_ITERATOR;
            }

            return new MenuListIterator(popups);
        }

        List<MenuComponent> list = ComponentIterators.list(
                (UIComponent) component, MenuComponent.class);
        if (list.isEmpty()) {
            return EMPTY_MENU_ITERATOR;
        }

        return new MenuListIterator(list);
    }

    public static final IComponentValueType MENU_VALUE_TYPE = new IComponentValueType() {

        public Object createNewValue(UIComponent component) {
            return new OrderedSet<Object>();
        }
    };
}
