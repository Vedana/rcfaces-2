/*
 * $Id: DefaultItem.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.item;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.rcfaces.core.component.capability.IAcceleratorKeyCapability;
import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.component.capability.IAlternateTextCapability;
import org.rcfaces.core.component.capability.IClientDataCapability;
import org.rcfaces.core.component.capability.IDragAndDropEffects;
import org.rcfaces.core.component.capability.IDraggableCapability;
import org.rcfaces.core.component.capability.IDroppableCapability;
import org.rcfaces.core.component.capability.IHorizontalTextPositionCapability;
import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.component.capability.IImageSizeCapability;
import org.rcfaces.core.component.capability.IImmediateCapability;
import org.rcfaces.core.component.capability.IInputTypeCapability;
import org.rcfaces.core.component.capability.ILookAndFeelCapability;
import org.rcfaces.core.component.capability.IMenuPopupIdCapability;
import org.rcfaces.core.component.capability.IRadioGroupCapability;
import org.rcfaces.core.component.capability.IServerDataCapability;
import org.rcfaces.core.component.capability.IStyleClassCapability;
import org.rcfaces.core.component.capability.IVisibilityCapability;
import org.rcfaces.core.component.capability.IWidthCapability;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.component.IExpandImageAccessors;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.component.IStatesImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
class DefaultItem extends SelectItemGroup {

    private static final long serialVersionUID = -3799667579566545958L;

    protected static final SelectItem SELECT_ITEMS_EMPTY_ARRAY[] = new SelectItem[0];

    private Map<String, Object> serverDatas;

    private Map<String, String> clientDatas;

    private String imageURL;

    private String disabledImageURL;

    private String hoverImageURL;

    private String selectedImageURL;

    private String expandedImageURL;

    private int inputType;

    private String styleClass;

    private String groupName;

    private int imageWidth;

    private int imageHeight;

    private boolean visible = true;

    private boolean immediate = true;

    private String borderType;

    private int textPosition = IHorizontalTextPositionCapability.UNKNOWN_POSITION;

    private String accessKey;

    private String lookId;

    private String acceleratorKey;

    private String menuPopupId;

    private String width;

    private String alternateText;

    private int dragEffects = IDragAndDropEffects.UNKNOWN_DND_EFFECT;

    private String dragTypes[];

    private int dropEffects = IDragAndDropEffects.UNKNOWN_DND_EFFECT;

    private String dropTypes[];

    public DefaultItem() {
    }

    public DefaultItem(String label) {
        super(label);
    }

    public DefaultItem(String label, String description, boolean disabled,
            SelectItem items[]) {
        super(label, description, disabled, items);
    }

    public DefaultItem(ISelectItemGroup selectItem) {
        super(selectItem.getLabel());

        String description = selectItem.getDescription();
        if (description != null) {
            setDescription(description);
        }

        if (selectItem.isDisabled()) {
            setDisabled(true);
        }

        SelectItem children[] = selectItem.getSelectItems();
        if (children != null) {
            setSelectItems(children);
        }

        Object itemValue = selectItem.getValue();
        if (itemValue != null) {
            setValue(itemValue);
        }

        if (selectItem instanceof IInputTypeItem) {
            setInputType(((IInputTypeItem) selectItem).getInputType());

            if (selectItem instanceof IGroupSelectItem) {
                String groupName = ((IGroupSelectItem) selectItem)
                        .getGroupName();
                if (groupName != null) {
                    setGroupName(groupName);
                }
            }
        }

        if (selectItem instanceof IImagesItem) {
            IImagesItem imagesItem = (IImagesItem) selectItem;
            setImageURL(imagesItem.getImageURL());
            setDisabledImageURL(imagesItem.getDisabledImageURL());
            setHoverImageURL(imagesItem.getHoverImageURL());
            setSelectedImageURL(imagesItem.getSelectedImageURL());
            setExpandedImageURL(imagesItem.getExpandedImageURL());
        }

        if (selectItem instanceof IStyleClassItem) {
            setStyleClass(((IStyleClassItem) selectItem).getStyleClass());
        }

        if (selectItem instanceof ILookAndFeelItem) {
            setLookId(((ILookAndFeelItem) selectItem).getLookId());
        }

        if (selectItem instanceof IClientDataItem) {
            Map<String, String> map = ((IClientDataItem) selectItem)
                    .getClientDataMap();
            if (map.isEmpty() == false) {
                getClientDataMap().putAll(map);
            }
        }

        if (selectItem instanceof IServerDataItem) {
            Map<String, Object> map = ((IServerDataItem) selectItem)
                    .getServerDataMap();
            if (map.isEmpty() == false) {
                getServerDataMap().putAll(map);
            }
        }

        if (selectItem instanceof IVisibleItem) {
            if (((IVisibleItem) selectItem).isVisible() == false) {
                setVisible(false);
            }
        }

        if (selectItem instanceof IImmediateItem) {
            if (((IImmediateItem) selectItem).isImmediate() == false) {
                setImmediate(false);
            }
        }

        if (selectItem instanceof IAccessKeyItem) {
            setAccessKey(((IAccessKeyItem) selectItem).getAccessKey());
        }

        if (selectItem instanceof IAcceleratorKeyItem) {
            setAcceleratorKey(((IAcceleratorKeyItem) selectItem)
                    .getAcceleratorKey());
        }

        if (selectItem instanceof IMenuPopupIdItem) {
            setMenuPopupId(((IMenuPopupIdItem) selectItem).getMenuPopupId());
        }

        if (selectItem instanceof IWidthItem) {
            setWidth(((IWidthItem) selectItem).getWidth());
        }

        if (selectItem instanceof IAlternateTextItem) {
            setAlternateText(((IAlternateTextItem) selectItem)
                    .getAlternateText());
        }

        if (selectItem instanceof SelectItem) {
            if (selectItem instanceof IDraggableItem) {
                IDraggableItem draggableItem = (IDraggableItem) selectItem;

                setDragEffects(draggableItem
                        .getDragEffects((SelectItem) selectItem));

                setDragTypes(draggableItem
                        .getDragTypes((SelectItem) selectItem));
            }

            if (selectItem instanceof IDroppableItem) {
                IDroppableItem droppableItem = (IDroppableItem) selectItem;

                setDropEffects(droppableItem
                        .getDropEffects((SelectItem) selectItem));

                setDropTypes(droppableItem
                        .getDropTypes((SelectItem) selectItem));
            }
        }
    }

    public DefaultItem(UISelectItem selectItemComponent) {
        super(selectItemComponent.getItemLabel(), selectItemComponent
                .getItemDescription(), selectItemComponent.isItemDisabled(),
                SELECT_ITEMS_EMPTY_ARRAY);

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Object itemValue = selectItemComponent.getItemValue();
        if (itemValue != null) {
            setValue(itemValue);
        }
        setInputType(selectItemComponent);

        if (selectItemComponent instanceof IStyleClassCapability) {
            setStyleClass(((IStyleClassCapability) selectItemComponent)
                    .getStyleClass());
        }

        if (selectItemComponent instanceof ILookAndFeelCapability) {
            setLookId(((ILookAndFeelCapability) selectItemComponent)
                    .getLookId());
        }

        if (selectItemComponent instanceof IImageCapability) {
            IContentAccessors contentAccessors = ((IImageCapability) selectItemComponent)
                    .getImageAccessors();

            if (contentAccessors instanceof IImageAccessors) {
                IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;

                IContentAccessor contentAccessor = imageAccessors
                        .getImageAccessor();
                if (contentAccessor != null) {
                    setImageURL(contentAccessor.resolveURL(facesContext, null,
                            null));
                }

                if (imageAccessors instanceof IStatesImageAccessors) {
                    IStatesImageAccessors statesImageAccessors = (IStatesImageAccessors) imageAccessors;

                    contentAccessor = statesImageAccessors
                            .getDisabledImageAccessor();
                    if (contentAccessor != null) {
                        setDisabledImageURL(contentAccessor.resolveURL(
                                facesContext, null, null));
                    }

                    contentAccessor = statesImageAccessors
                            .getHoverImageAccessor();
                    if (contentAccessor != null) {
                        setHoverImageURL(contentAccessor.resolveURL(
                                facesContext, null, null));
                    }

                    contentAccessor = statesImageAccessors
                            .getSelectedImageAccessor();
                    if (contentAccessor != null) {
                        setSelectedImageURL(contentAccessor.resolveURL(
                                facesContext, null, null));
                    }

                    if (imageAccessors instanceof IExpandImageAccessors) {
                        IExpandImageAccessors expandImageAccessors = (IExpandImageAccessors) imageAccessors;

                        contentAccessor = expandImageAccessors
                                .getExpandedImageAccessor();
                        if (contentAccessor != null) {
                            setExpandedImageURL(contentAccessor.resolveURL(
                                    facesContext, null, null));
                        }
                    }

                }
            }
        }

        if (selectItemComponent instanceof IImageSizeCapability) {
            IImageSizeCapability imageSizeCapability = (IImageSizeCapability) selectItemComponent;

            setImageWidth(imageSizeCapability.getImageWidth());
            setImageHeight(imageSizeCapability.getImageHeight());
        }

        if (selectItemComponent instanceof IServerDataCapability) {
            IServerDataCapability serverDataCapability = (IServerDataCapability) selectItemComponent;

            if (serverDataCapability.getServerDataCount() > 0) {
                Map<String, Object> map = serverDataCapability
                        .getServerDataMap();

                getServerDataMap().putAll(map);
            }
        }

        if (selectItemComponent instanceof IClientDataCapability) {
            IClientDataCapability clientDataCapability = (IClientDataCapability) selectItemComponent;

            if (clientDataCapability.getClientDataCount() > 0) {
                Map<String, String> map = clientDataCapability
                        .getClientDataMap();

                getClientDataMap().putAll(map);
            }
        }

        if (selectItemComponent.isRendered() == false) {
            setVisible(false);

        } else if (selectItemComponent instanceof IVisibilityCapability) {
            if (((IVisibilityCapability) selectItemComponent).isVisible() == false) {
                setVisible(false);
            }
        }

        if (selectItemComponent instanceof IAccessKeyCapability) {
            setAccessKey(((IAccessKeyCapability) selectItemComponent)
                    .getAccessKey());
        }

        if (selectItemComponent instanceof IAcceleratorKeyCapability) {
            setAcceleratorKey(((IAcceleratorKeyCapability) selectItemComponent)
                    .getAcceleratorKey());
        }

        if (selectItemComponent instanceof IMenuPopupIdCapability) {
            setMenuPopupId(((IMenuPopupIdCapability) selectItemComponent)
                    .getMenuPopupId());
        }

        if (selectItemComponent instanceof IWidthCapability) {
            setWidth(((IWidthCapability) selectItemComponent).getWidth());
        }

        if (selectItemComponent instanceof IAlternateTextCapability) {
            setAlternateText(((IAlternateTextCapability) selectItemComponent)
                    .getAlternateText());
        }

        if (selectItemComponent instanceof IImmediateCapability) {
            setImmediate(((IImmediateCapability) selectItemComponent)
                    .isImmediate());
        }

        if (selectItemComponent instanceof IDraggableCapability) {
            IDraggableCapability draggableCapability = (IDraggableCapability) selectItemComponent;

            setDragEffects(draggableCapability.getDragEffects());
            setDragTypes(draggableCapability.getDragTypes());
        }

        if (selectItemComponent instanceof IDroppableCapability) {
            IDroppableCapability droppableCapability = (IDroppableCapability) selectItemComponent;

            setDropEffects(droppableCapability.getDropEffects());
            setDropTypes(droppableCapability.getDropTypes());
        }

    }

    public String getImageURL() {
        return imageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getHoverImageURL()
     */
    public String getHoverImageURL() {
        return hoverImageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getSelectedImageURL()
     */
    public String getSelectedImageURL() {
        return selectedImageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getExpandedImageURL()
     */
    public String getExpandedImageURL() {
        return expandedImageURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.model.IImagesSelectItem#getDisabledImageURL()
     */
    public String getDisabledImageURL() {
        return disabledImageURL;
    }

    public void setDisabledImageURL(String disabledImageURL) {
        this.disabledImageURL = disabledImageURL;
    }

    public void setExpandedImageURL(String expandedImageURL) {
        this.expandedImageURL = expandedImageURL;
    }

    public void setHoverImageURL(String hoverImageURL) {
        this.hoverImageURL = hoverImageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setSelectedImageURL(String selectedImageURL) {
        this.selectedImageURL = selectedImageURL;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public final String getStyleClass() {
        return styleClass;
    }

    public final void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
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

    public final String getGroupName() {
        return groupName;
    }

    public final void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public final int getImageHeight() {
        return imageHeight;
    }

    public final void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public final int getImageWidth() {
        return imageWidth;
    }

    public final void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public final String getLookId() {
        return lookId;
    }

    public final void setLookId(String lookId) {
        this.lookId = lookId;
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

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public final boolean isImmediate() {
        return immediate;
    }

    public final void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public final String getBorderType() {
        return borderType;
    }

    public final void setBorderType(String borderType) {
        this.borderType = borderType;
    }

    public final int getTextPosition() {
        return textPosition;
    }

    public final void setTextPosition(int textPosition) {
        this.textPosition = textPosition;
    }

    public final String getAccessKey() {
        return accessKey;
    }

    public final void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public final String getAcceleratorKey() {
        return acceleratorKey;
    }

    public final void setAcceleratorKey(String acceleratorKey) {
        this.acceleratorKey = acceleratorKey;
    }

    public final String getMenuPopupId() {
        return menuPopupId;
    }

    public final void setMenuPopupId(String menuPopupId) {
        this.menuPopupId = menuPopupId;
    }

    protected void setInputType(UIComponent inputComponent) {
        if (inputComponent instanceof IInputTypeCapability) {
            setInputType(((IInputTypeCapability) inputComponent).getInputType());
        }

        if (inputComponent instanceof IRadioGroupCapability) {
            String groupName = ((IRadioGroupCapability) inputComponent)
                    .getGroupName();
            if (groupName != null) {
                setGroupName(groupName);
            }
        }
    }

    public final String getWidth() {
        return width;
    }

    public final void setWidth(String width) {
        this.width = width;
    }

    public final String getAlternateText() {
        return alternateText;
    }

    public final void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    public int getDragEffects() {
        return dragEffects;
    }

    public void setDragEffects(int dragEffects) {
        this.dragEffects = dragEffects;
    }

    public String[] getDragTypes() {
        return dragTypes;
    }

    public void setDragTypes(String[] dragTypes) {
        this.dragTypes = dragTypes;
    }

    public int getDragEffects(SelectItem dragItem) {
        if (dragItem == null || equals(getValue(), dragItem.getValue())) {
            return dragEffects;
        }

        return IDragAndDropEffects.UNKNOWN_DND_EFFECT;
    }

    public String[] getDragTypes(SelectItem dragItem) {
        if (dragItem == null || equals(getValue(), dragItem.getValue())) {
            return dragTypes;
        }

        return null;
    }

    public int getDropEffects() {
        return dropEffects;
    }

    public void setDropEffects(int dropEffects) {
        this.dropEffects = dropEffects;
    }

    public String[] getDropTypes() {
        return dropTypes;
    }

    public void setDropTypes(String[] dropTypes) {
        this.dropTypes = dropTypes;
    }

    public int getDropEffects(SelectItem dropItem) {
        if (dropItem == null || equals(getValue(), dropItem.getValue())) {
            return dropEffects;
        }

        return IDragAndDropEffects.UNKNOWN_DND_EFFECT;
    }

    public String[] getDropTypes(SelectItem dropItem) {
        if (dropItem == null || equals(getValue(), dropItem.getValue())) {
            return dropTypes;
        }

        return null;
    }

    private boolean equals(Object value1, Object value2) {
        if (value1 == value2) {
            return true;
        }

        return value1 != null && value1.equals(value2);
    }

}
