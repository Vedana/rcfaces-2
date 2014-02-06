/*
 * $Id: DateItem.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.DateItemComponent;
import org.rcfaces.core.component.capability.IClientDataCapability;
import org.rcfaces.core.component.capability.IServerDataCapability;
import org.rcfaces.core.component.capability.IStyleClassCapability;
import org.rcfaces.core.internal.converter.LiteralDateConverter;
import org.rcfaces.core.internal.util.Convertor;
import org.rcfaces.core.lang.Period;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class DateItem extends SelectItem implements IDateItem {

    private static final long serialVersionUID = -2332673361111286166L;

    private String styleClass;

    private Map clientDatas;

    private Map serverDatas;

    private String menuPopupId;

    public DateItem() {
    }

    public DateItem(IDateItem dateItem) {
        super(dateItem.getValue(), dateItem.getLabel(), dateItem
                .getDescription(), dateItem.isDisabled());

        setStyleClass(dateItem.getStyleClass());

        Map map = dateItem.getClientDataMap();
        if (map.isEmpty() == false) {
            getClientDataMap().putAll(map);
        }

        map = dateItem.getServerDataMap();
        if (map.isEmpty() == false) {
            getServerDataMap().putAll(map);
        }
    }

    public DateItem(UISelectItem component) {
        super(convertValue((DateItemComponent) component), component
                .getItemLabel(), component.getItemDescription(), component
                .isItemDisabled());

        if (component instanceof IStyleClassCapability) {
            IStyleClassCapability dateItemComponent = (IStyleClassCapability) component;

            styleClass = dateItemComponent.getStyleClass();
        }

        if (component instanceof IServerDataCapability) {
            IServerDataCapability serverDataCapability = (IServerDataCapability) component;

            if (serverDataCapability.getServerDataCount() > 0) {
                Map map = serverDataCapability.getServerDataMap();

                getServerDataMap().putAll(map);
            }
        }

        if (component instanceof IClientDataCapability) {
            IClientDataCapability clientDataCapability = (IClientDataCapability) component;

            if (clientDataCapability.getClientDataCount() > 0) {
                Map map = clientDataCapability.getClientDataMap();

                getClientDataMap().putAll(map);
            }
        }
    }

    private static Object convertValue(DateItemComponent component) {
        Object value = component.getItemValue();

        if (value == null || (value instanceof Date)
                || (value instanceof Period)) {
            return value;
        }

        if (value instanceof String) {
            return LiteralDateConverter.SINGLETON.getAsObject(null, component,
                    (String) value);
        }

        return Convertor.convert(value, Date.class);
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public boolean isClientDataEmpty() {
        if (clientDatas == null) {
            return true;
        }

        return clientDatas.isEmpty();
    }

    public Map getClientDataMap() {
        if (clientDatas == null) {
            clientDatas = new HashMap();
        }

        return clientDatas;
    }

    public boolean isServerDataEmpty() {
        if (serverDatas == null) {
            return true;
        }

        return serverDatas.isEmpty();
    }

    public Map getServerDataMap() {
        if (serverDatas == null) {
            serverDatas = new HashMap();
        }

        return serverDatas;
    }

    public final String getMenuPopupId() {
        return menuPopupId;
    }

    public final void setMenuPopupId(String menuPopupId) {
        this.menuPopupId = menuPopupId;
    }

}