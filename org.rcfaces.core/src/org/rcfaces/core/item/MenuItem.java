/*
 * $Id: MenuItem.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.MenuCheckItemComponent;
import org.rcfaces.core.component.MenuItemComponent;
import org.rcfaces.core.component.MenuRadioItemComponent;
import org.rcfaces.core.component.capability.IInputTypeCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */
public class MenuItem extends DefaultItem implements IMenuItem {

    private static final long serialVersionUID = 1262314873491593499L;

    public MenuItem() {
    }

    public MenuItem(String label, String description, boolean disabled,
            SelectItem items[]) {
        super(label, description, disabled, items);
    }

    public MenuItem(IMenuItem menuItem) {
        super(menuItem);
    }

    public MenuItem(MenuItemComponent menuItemComponent) {
        super(menuItemComponent);
    }

    @Override
    protected void setInputType(UIComponent inputComponent) {

        int type = IInputTypeCapability.AS_PUSH_BUTTON;

        if (inputComponent instanceof MenuRadioItemComponent) {
            type = IInputTypeCapability.AS_RADIO_BUTTON;
            // Attention un RADIO est aussi un CHECK !

            String groupName = ((MenuRadioItemComponent) inputComponent)
                    .getGroupName();
            if (groupName != null) {
                setGroupName(groupName);
            }

        } else if (inputComponent instanceof MenuCheckItemComponent) {
            type = IInputTypeCapability.AS_CHECK_BUTTON;
        }

        setInputType(type);
    }

}
