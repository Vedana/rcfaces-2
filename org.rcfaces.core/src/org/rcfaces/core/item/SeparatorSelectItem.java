/*
 * $Id: SeparatorSelectItem.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import javax.faces.model.SelectItem;

import org.rcfaces.core.component.capability.IInputTypeCapability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class SeparatorSelectItem extends SelectItem implements ISeparatorItem {

    private static final long serialVersionUID = -5825985202702986997L;

    private static final String SEPARATOR_KEY = "$-camelia#separator-$";

    public static final SelectItem SEPARATOR = new SeparatorSelectItem() {

        private static final long serialVersionUID = 3542510884321447395L;

        public Object getValue() {
            return SEPARATOR_KEY;
        }

        public void setValue(Object value) {
            throw new IllegalStateException(
                    "Can not change value of Separator item !");
        }
    };

    public static final boolean isSeparator(SelectItem selectItem) {
        if (selectItem == SEPARATOR) {
            return true;
        }
        if (selectItem instanceof ISeparatorItem) {
            if (((ISeparatorItem) selectItem).isSeparator()) {
                return true;
            }
        }
        if (selectItem instanceof IInputTypeItem) {
            if (((IInputTypeItem) selectItem).getInputType() == IInputTypeCapability.AS_SEPARATOR) {
                return true;
            }
        }

        if (SEPARATOR_KEY.equals(selectItem.getValue())) {
            return true;
        }

        return false;
    }

    public boolean isSeparator() {
        return true;
    }

}
