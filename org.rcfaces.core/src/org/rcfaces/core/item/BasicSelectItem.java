/*
 * $Id: BasicSelectItem.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.capability.IAcceleratorKeyCapability;
import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.component.capability.IClientDataCapability;
import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.component.capability.IDraggableCapability;
import org.rcfaces.core.component.capability.IInputTypeCapability;
import org.rcfaces.core.component.capability.IRadioGroupCapability;
import org.rcfaces.core.component.capability.IServerDataCapability;
import org.rcfaces.core.component.capability.IStyleClassCapability;
import org.rcfaces.core.component.capability.IToolTipTextCapability;
import org.rcfaces.core.component.capability.IVisibilityCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public class BasicSelectItem extends SelectItem implements ISelectItem,
        IAccessKeyItem, IAcceleratorKeyItem, IGroupSelectItem, IInputTypeItem,
        IVisibleItem, IServerDataItem, IClientDataItem, IStyleClassItem {

    private static final long serialVersionUID = 6953469102413843158L;

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    private Map<String, Object> serverDatas;

    private Map<String, String> clientDatas;

    private String accessKey;

    private String acceleratorKey;

    private String groupName;

    private boolean visible = true;

    private int inputType;

    private String styleClass;

    private int dragEffects = IDragAndDropEffects.UNKNOWN_DND_EFFECT;

    private String[] dragTypes;

    public BasicSelectItem() {
    }

    public BasicSelectItem(Object value) {
        super(value);
    }

    public BasicSelectItem(Object value, String label) {
        super(value, label);
    }

    public BasicSelectItem(Object value, String label, String description) {
        super(value, label, description);
    }

    public BasicSelectItem(Object value, String label, String description,
            boolean disabled) {
        super(value, label, description, disabled);
    }

    public BasicSelectItem(ISelectItem selectItem) {
        super(selectItem.getValue(), selectItem.getLabel(), selectItem
                .getDescription(), selectItem.isDisabled());
    }

    public BasicSelectItem(UISelectItem component) {
        super(getValue(component), component.getItemLabel(), component
                .getItemDescription(), component.isItemDisabled());

        int s = 0; // IInputTypeCapability.AS_PUSH_BUTTON; // Pas de valeur par
        // défaut

        if (component instanceof IAccessKeyCapability) {
            setAccessKey(((IAccessKeyCapability) component).getAccessKey());

        } else {
            setAccessKey(null);
        }

        if (component instanceof IAcceleratorKeyCapability) {
            setAcceleratorKey(((IAcceleratorKeyCapability) component)
                    .getAcceleratorKey());

        } else {
            setAcceleratorKey(null);
        }

        if (component instanceof IRadioGroupCapability) {
            setGroupName(((IRadioGroupCapability) component).getGroupName());
            s = IInputTypeCapability.AS_RADIO_BUTTON;

        } else {
            setGroupName(null);
        }

        if (component.isRendered() == false) {
            setVisible(false);

        } else if (component instanceof IVisibilityCapability) {
            Boolean b = ((IVisibilityCapability) component).getVisibleState();

            setVisible(Boolean.FALSE.equals(b) == false);

        } else {
            setVisible(true);
        }

        if (component instanceof IServerDataCapability) {
            IServerDataCapability serverDataCapability = (IServerDataCapability) component;

            if (serverDataCapability.getServerDataCount() > 0) {
                Map<String, Object> map = serverDataCapability
                        .getServerDataMap();

                getServerDataMap().putAll(map);
            }
        }

        if (component instanceof IClientDataCapability) {
            IClientDataCapability clientDataCapability = (IClientDataCapability) component;

            if (clientDataCapability.getClientDataCount() > 0) {
                Map<String, String> map = clientDataCapability
                        .getClientDataMap();

                getClientDataMap().putAll(map);
            }
        }

        if (getDescription() == null
                && (component instanceof IToolTipTextCapability)) {
            setDescription(((IToolTipTextCapability) component)
                    .getToolTipText());
        }

        setInputType(s);

        if (component instanceof IStyleClassCapability) {
            IStyleClassCapability styleClassCapability = (IStyleClassCapability) component;

            setStyleClass(styleClassCapability.getStyleClass());
        }

        if (component instanceof IDraggableCapability) {
            IDraggableCapability draggableItemCapability = (IDraggableCapability) component;

            setDragTypes(draggableItemCapability.getDragTypes());
            setDragEffects(draggableItemCapability.getDragEffects());
        }
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

    public int getInputType() {
        return inputType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public final void setAcceleratorKey(String acceleratorKey) {
        this.acceleratorKey = acceleratorKey;
    }

    public final void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setInputType(int style) {
        this.inputType = style;
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

    public Map<String, Object> getServerDataMap() {
        if (serverDatas == null) {
            serverDatas = new HashMap<String, Object>();
        }

        return serverDatas;
    }

    public boolean isClientDataEmpty() {
        if (clientDatas == null) {
            return true;
        }

        return clientDatas.isEmpty();
    }

    public Map<String, String> getClientDataMap() {
        if (clientDatas == null) {
            clientDatas = new HashMap<String, String>();
        }

        return clientDatas;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getAcceleratorKey() {
        return acceleratorKey;
    }

    public int getDragEffects(SelectItem dragItem) {
        if (dragItem == null || equals(getValue(), dragItem.getValue())) {
            return dragEffects;
        }

        return IDragAndDropEffects.UNKNOWN_DND_EFFECT;
    }

    public void setDragEffects(int dragEffects) {
        this.dragEffects = dragEffects;
    }

    public String[] getDragTypes(SelectItem dragItem) {
        if (dragItem == null || equals(getValue(), dragItem.getValue())) {
            return dragTypes;
        }

        return null;
    }

    public void setDragTypes(String[] dragTypes) {
        this.dragTypes = dragTypes;
    }

    private boolean equals(Object value1, Object value2) {
        if (value1 == value2) {
            return true;
        }

        return value1 != null && value1.equals(value2);
    }
}