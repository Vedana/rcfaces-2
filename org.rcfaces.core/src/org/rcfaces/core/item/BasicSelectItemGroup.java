/*
 * $Id: BasicSelectItemGroup.java,v 1.4 2014/01/07 13:48:20 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2014/01/07 13:48:20 $
 */
public class BasicSelectItemGroup extends SelectItemGroup implements
        ISelectItemGroup, IAccessKeyItem, IAcceleratorKeyItem,
        IGroupSelectItem, IInputTypeItem, IVisibleItem, IServerDataItem,
        IClientDataItem, IStyleClassItem {

    private static final long serialVersionUID = 6953469102413843158L;

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    private Map serverDatas;

    private Map clientDatas;

    private String accessKey;

    private String acceleratorKey;

    private String groupName;

    private boolean checked;

    private boolean visible = true;

    private int inputType;

    private String styleClass;

    public BasicSelectItemGroup() {
    }

    public BasicSelectItemGroup(String label) {
        super(label);
    }

    public BasicSelectItemGroup(Object value, String label, String description,
            boolean disabled, SelectItem selectItems[]) {
        super(label, description, disabled, selectItems);
        
        setValue(value);
    }

    public BasicSelectItemGroup(ISelectItem selectItem) {
        super(selectItem.getLabel());

        String description = selectItem.getDescription();
        if (description != null) {
            setDescription(description);
        }

        if (selectItem.isDisabled()) {
            setDisabled(true);
        }

        setValue(selectItem.getValue());
    }

    public BasicSelectItemGroup(ISelectItemGroup selectItemGroup) {
        super(selectItemGroup.getLabel());

        String description = selectItemGroup.getDescription();
        if (description != null) {
            setDescription(description);
        }

        if (selectItemGroup.isDisabled()) {
            setDisabled(true);
        }

        SelectItem children[] = selectItemGroup.getSelectItems();
        if (children != null) {
            setSelectItems(children);
        }

        setValue(selectItemGroup.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IAccessKeySelectItem#getAccessKey()
     */
    public String getAccessKey() {
        return accessKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public static Object getValue(UISelectItem component) {
        Object value = component.getItemValue();
        if (value != null) {
            return value;
        }

        // Ben OUI on retourne l'ID !!!!
        // En tout cas il ne faut pas retourner NULL !
        return component.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.ICheckSelectItem#isChecked()
     */
    public boolean isChecked() {
        return checked;
    }

    public int getInputType() {
        return inputType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public String getStyleClass() {
        return styleClass;
    }

    public String getAcceleratorKey() {
        return acceleratorKey;
    }
}