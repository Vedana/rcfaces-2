package org.rcfaces.renderkit.html.internal.decorator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ImageButtonComponent;
import org.rcfaces.core.component.ImageCheckButtonComponent;
import org.rcfaces.core.component.ImageComboComponent;
import org.rcfaces.core.component.ImageRadioButtonComponent;
import org.rcfaces.core.component.ImageResetButtonComponent;
import org.rcfaces.core.component.ImageSubmitButtonComponent;
import org.rcfaces.core.component.ItemsListComponent;
import org.rcfaces.core.component.ToolItemComponent;
import org.rcfaces.core.component.ToolItemSeparatorComponent;
import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.component.capability.IBorderTypeCapability;
import org.rcfaces.core.component.capability.IDisabledCapability;
import org.rcfaces.core.component.capability.IExpandImageCapability;
import org.rcfaces.core.component.capability.IHiddenModeCapability;
import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.component.capability.IImageSizeCapability;
import org.rcfaces.core.component.capability.IInputTypeCapability;
import org.rcfaces.core.component.capability.ILookAndFeelCapability;
import org.rcfaces.core.component.capability.ISizeCapability;
import org.rcfaces.core.component.capability.IStatesImageCapability;
import org.rcfaces.core.component.capability.IStyleClassCapability;
import org.rcfaces.core.component.capability.ITextCapability;
import org.rcfaces.core.component.capability.ITextPositionCapability;
import org.rcfaces.core.component.capability.IToolTipTextCapability;
import org.rcfaces.core.component.capability.IVisibilityCapability;
import org.rcfaces.core.event.SelectionEvent;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.item.IAccessKeyItem;
import org.rcfaces.core.item.IBorderTypeItem;
import org.rcfaces.core.item.ICheckSelectItem;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IGroupSelectItem;
import org.rcfaces.core.item.IImageSizeItem;
import org.rcfaces.core.item.IImagesItem;
import org.rcfaces.core.item.IInputTypeItem;
import org.rcfaces.core.item.ILookAndFeelItem;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.core.item.ITextPositionItem;
import org.rcfaces.core.item.IToolItem;
import org.rcfaces.core.item.IVisibleItem;
import org.rcfaces.core.item.IWidthItem;
import org.rcfaces.core.item.SeparatorSelectItem;
import org.rcfaces.core.item.ToolItem;
import org.rcfaces.renderkit.html.internal.IHtmlRequestContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.renderer.ToolBarRenderer;

/**
 * Decorator for itemsList (inspired by itemsToolFolder)
 * 
 * @author Fred Lefevere-Laoide
 * @see ItemsToolFolderDecorator
 */
public class ItemsListDecorator extends AbstractSelectItemsDecorator {
    

    private static final Log LOG = LogFactory.getLog(ItemsListDecorator.class);

    private static final String DISABLED_ITEMS = "disabledItems";

    private static final String ENABLED_ITEMS = "enabledItems";

    private static final int DEFAULT_INPUT_TYPE = IInputTypeCapability.AS_PUSH_BUTTON;

    private static final String COMPONENT_ID_TO_VALUE_PROPERTY = "org.rcfaces.html.ITEMS_CID";

    private final String borderType;

    private final List<ItemsMenuDecorator> menuDecoratorStack = new ArrayList<ItemsMenuDecorator>(
            8);

    private final List<String> itemsId = new ArrayList<String>(8);

    private final Map<String, String> itemIdToClientId = new HashMap<String, String>(
            8);

    private boolean itemPaddingSetted;

    private int itemPadding;

    private String defaultImageURL;

    private String defaultDisabledImageURL;

    private String defaultHoverImageURL;

    private String defaultSelectedImageURL;

    private int defaultInputType = DEFAULT_INPUT_TYPE;

    private String defaultItemStyleClass;

    private String defaultItemLookId;

    private String defaultItemGroupName;

    /**
     * Constructor
     * 
     * @param facesContext
     *            TODO
     * @param component
     *            component ItemsList
     */
    public ItemsListDecorator(FacesContext facesContext, UIComponent component) {
        super(component, null);

        ItemsListComponent itemsListComponent = (ItemsListComponent) component;

        defaultInputType = itemsListComponent
                .getDefaultItemInputType(facesContext);

        defaultItemLookId = itemsListComponent
                .getDefaultItemLookId(facesContext);

        defaultItemStyleClass = itemsListComponent
                .getDefaultItemStyleClass(facesContext);

        defaultItemGroupName = itemsListComponent
                .getDefaultItemGroupName(facesContext);

        if (itemsListComponent.isBorderTypeSetted()) {
            borderType = itemsListComponent.getBorderType(facesContext);

        } else {
            borderType = null;
        }

        if (itemsListComponent.isItemPaddingSetted()) {
            itemPaddingSetted = true;

            itemPadding = itemsListComponent.getItemPadding(facesContext);
        }

        if (itemsListComponent.isDefaultImageURLSetted()) {
            defaultImageURL = itemsListComponent
                    .getDefaultImageURL(facesContext);
        } else {
            defaultImageURL = null;
        }
        if (itemsListComponent.isDefaultDisabledImageURLSetted()) {
            defaultDisabledImageURL = itemsListComponent
                    .getDefaultDisabledImageURL(facesContext);
        } else {
            defaultDisabledImageURL = null;
        }
        if (itemsListComponent.isDefaultHoverImageURLSetted()) {
            defaultHoverImageURL = itemsListComponent
                    .getDefaultHoverImageURL(facesContext);
        } else {
            defaultHoverImageURL = null;
        }
        if (itemsListComponent.isDefaultSelectedImageURLSetted()) {
            defaultSelectedImageURL = itemsListComponent
                    .getDefaultSelectedImageURL(facesContext);
        } else {
            defaultSelectedImageURL = null;
        }
    }

    /**
     * Starting
     */
    protected void preEncodeContainer() throws WriterException {

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        super.preEncodeContainer();
    }

    /**
     * @param component
     *            component ItemsList
     * @param selectItem
     *            selectItem
     * @param hasChild
     *            hasCHild
     * @param isVisible
     *            isVisible
     * @return int indicating wether the node should be skipped or not
     */
    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (javaScriptWriter != null) {
            return encodeToolItemPopupBegin(component, selectItem, hasChild,
                    isVisible);
        }

        if (SeparatorSelectItem.isSeparator(selectItem)) {
            return SKIP_NODE;
        }

        if (getContext().getDepth() == 1) {
            return encodeToolItemBegin(component, selectItem, hasChild);
        }

        // encodeToolItemBegin(component, selectItem, hasChild);

        // C'est un popup menu ?

        return EVAL_NODE;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.renderkit.html.internal.decorator.ISelectItemNodeWriter#
     * encodeNodeEnd(javax.faces.component.UIComponent,
     * javax.faces.model.SelectItem, boolean, boolean)
     */
    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (javaScriptWriter != null) {
            // pas de Javascript

            int depth = getContext().getDepth();

            if (hasChild || depth > 1) {
                encodeToolItemPopupEnd(component, selectItem, hasChild,
                        isVisible);
            }
            return;
        }

    }

    /**
     * for popups (not used yet ...)
     * 
     * @param component
     *            component ItemsList
     * @param selectItem
     *            selectItem
     * @param hasChild
     *            hasChild
     * @param isVisible
     *            isVisible
     * @return int indicating wether the node should be skipped or not
     */
    protected int encodeToolItemPopupBegin(UIComponent component,
            SelectItem selectItem, boolean hasChild, boolean isVisible)
            throws WriterException {

        ItemsMenuDecorator menuDecorator = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Encode node BEGIN value='" + selectItem.getValue()
                    + "' hasChild=" + hasChild + " isVisible=" + isVisible
                    + "  detph=" + getContext().getDepth(), null);
        }
        if (getContext().getDepth() == 1) {
            String itemId = nextItemId();
            if (itemId == null) {
                return SKIP_NODE;
            }

            if (selectItem instanceof IVisibleItem) {
                if (((IVisibleItem) selectItem).isVisible() == false) {
                    return SKIP_NODE;
                }
            }

            int inputType = 0;

            if (SeparatorSelectItem.isSeparator(selectItem)) {
                inputType = IInputTypeCapability.AS_SEPARATOR;

            } else if (selectItem instanceof IInputTypeItem) {
                inputType = ((IInputTypeItem) selectItem).getInputType();
            }

            if (inputType == 0) {
                if (hasChild) {
                    inputType = IInputTypeCapability.AS_DROP_DOWN_MENU;

                } else {
                    inputType = defaultInputType;
                }
            }

            String selectItemVarName = javaScriptWriter
                    .getJavaScriptRenderContext().allocateVarName();

            javaScriptWriter.write(selectItemVarName).write('=')
                    .writeMethodCall("f_appendToolItem2").writeString(itemId)
                    .write(',');

            IObjectLiteralWriter objectLiteralWriter = javaScriptWriter
                    .writeObjectLiteral(false);

            Object siValue = selectItem.getValue();
            String selectItemValue = convertItemValue(javaScriptWriter
                    .getHtmlComponentRenderContext(), siValue);

            if (selectItemValue == null) {
                throw new FacesException(
                        "Item of a toolbar must have a value. ItemsListId="
                                + component.getId());
            }

            String itemCliendId = (String) itemIdToClientId.get(itemId);
            if (itemCliendId == null) {
                itemCliendId = itemId;
            }

            Map<String, String> componentIdToValue = (Map<String, String>) getComponent()
                    .getAttributes().get(COMPONENT_ID_TO_VALUE_PROPERTY);
            if (componentIdToValue == null) {
                componentIdToValue = new HashMap<String, String>(8);

                getComponent().getAttributes().put(
                        COMPONENT_ID_TO_VALUE_PROPERTY, componentIdToValue);
            }

            componentIdToValue.put(itemCliendId, selectItemValue);

            if (inputType == IInputTypeCapability.AS_SEPARATOR) {
                objectLiteralWriter.writeSymbol("_inputType").writeInt(
                        inputType);

            } else {
                objectLiteralWriter.writeSymbol("_value").writeString(
                        selectItemValue);

                if (inputType != DEFAULT_INPUT_TYPE) {
                    objectLiteralWriter.writeSymbol("_inputType").writeInt(
                            inputType);
                }
            }

            if (selectItem.getLabel() != null) {
                objectLiteralWriter.writeSymbol("_label").writeString(
                        selectItem.getLabel());
            }

            if (selectItem.getDescription() != null) {
                objectLiteralWriter.writeSymbol("_description").writeString(
                        selectItem.getDescription());
            }

            if (selectItem.isDisabled()) {
                objectLiteralWriter.writeSymbol("_disabled").writeBoolean(true);
            }

            /*
             * if (selectItem instanceof IStyleClassItem) { String styleClass =
             * ((IStyleClassItem) selectItem) .getStyleClass(); if (styleClass
             * != null) { javaScriptWriter.write(',').writeSymbol("_styleClass")
             * .write(':').writeString(styleClass); } }
             */

            if (selectItem instanceof IVisibleItem) {
                if (((IVisibleItem) selectItem).isVisible() == false) {
                    objectLiteralWriter.writeSymbol("_visible").writeBoolean(
                            false);
                }
            }

            if (selectItem instanceof IClientDataItem) {
                writeItemClientDatas((IClientDataItem) selectItem,
                        javaScriptWriter, null, null, objectLiteralWriter);
            }

            objectLiteralWriter.end().writeln(");");

            if (hasChild == false) {
                return SKIP_NODE;
            }

            String componentVarName = javaScriptWriter
                    .getJavaScriptRenderContext().allocateVarName();

            javaScriptWriter.write("var ").write(componentVarName).write('=')
                    .writeMethodCall("f_getItemComponent").write(
                            selectItemVarName).writeln(");");

            menuDecorator = pushMenuDecorator(componentVarName, itemId,
                    javaScriptWriter);

        } else {
            menuDecorator = peekMenuDecorator();

            menuDecorator.encodeNodeBegin(component, selectItem, hasChild,
                    isVisible);
        }

        return EVAL_NODE;
    }

    /**
     * for popups (not used ...)
     * 
     * @param component
     *            component ItemsList
     * @param selectItem
     *            selectItem
     * @param hasChild
     *            hasChild
     * @param isVisible
     *            isVisible
     */
    protected void encodeToolItemPopupEnd(UIComponent component,
            SelectItem selectItem, boolean hasChild, boolean isVisible)
            throws WriterException {

        ItemsMenuDecorator menuDecorator = peekMenuDecorator();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Encode node end value='" + selectItem.getValue()
                    + "' hasChild=" + hasChild + " isVisible=" + isVisible
                    + "  detph=" + getContext().getDepth(), null);
        }

        if (getContext().getDepth() == 1) {
            popupMenuDecorator();

        } else {
            menuDecorator.encodeNodeEnd(component, selectItem, hasChild,
                    isVisible);
        }
    }

    /**
     * 
     * @param selectItem
     *            selectItem
     * @return item Separator Width
     */
    protected int getToolItemSeparatorWidth(SelectItem selectItem) {
        return ToolBarRenderer.DEFAULT_TOOL_ITEM_SEPARATOR_WIDTH;
    }

    /**
     * Called for each item
     * 
     * @param component
     *            component ItemsList
     * @param selectItem
     *            selectItem
     * @param hasChild
     *            hasChild
     * @throws WriterException
     */
    private int encodeToolItemBegin(UIComponent component,
            SelectItem selectItem, boolean hasChild) throws WriterException {

        String itemId = allocateItemId();

        int style = 0;
        if (selectItem instanceof IInputTypeItem) {
            style = ((IInputTypeItem) selectItem).getInputType();

        } else if (selectItem instanceof IGroupSelectItem) {
            style = IInputTypeCapability.AS_RADIO_BUTTON;

        } else if (selectItem instanceof ICheckSelectItem) {
            style = IInputTypeCapability.AS_CHECK_BUTTON;
        }

        if (style == 0) {
            if (hasChild) {
                style = IInputTypeCapability.AS_DROP_DOWN_MENU;

            } else {
                style = defaultInputType;
            }
        }

        UIComponent itemComponent = null;

        switch (style) {
        case IInputTypeCapability.AS_RADIO_BUTTON:
            itemComponent = new ImageRadioButtonComponent(itemId);

            if (defaultItemGroupName != null) {
                ((ImageRadioButtonComponent) itemComponent)
                        .setGroupName(defaultItemGroupName);
            }

            break;

        case IInputTypeCapability.AS_CHECK_BUTTON:
            itemComponent = new ImageCheckButtonComponent(itemId);
            break;

        case IInputTypeCapability.AS_RESET_BUTTON:
            itemComponent = new ImageResetButtonComponent(itemId);
            break;

        case IInputTypeCapability.AS_SUBMIT_BUTTON:
            itemComponent = new ImageSubmitButtonComponent(itemId);
            break;

        case IInputTypeCapability.AS_DROP_DOWN_MENU:
            itemComponent = new ImageComboComponent(itemId);
            break;

        default:
            itemComponent = new ImageButtonComponent(itemId);
        }

        if (borderType != null
                && (itemComponent instanceof IBorderTypeCapability)) {
            ((IBorderTypeCapability) itemComponent).setBorderType(borderType);
        }

        String label = selectItem.getLabel();
        if (label != null && (itemComponent instanceof ITextCapability)) {
            ((ITextCapability) itemComponent).setText(label);

            if (itemComponent instanceof ITextPositionCapability) {
                int textPosition = ITextPositionCapability.UNKNOWN_POSITION;

                if (selectItem instanceof ITextPositionItem) {
                    textPosition = ((ITextPositionItem) selectItem)
                            .getTextPosition();
                }

                if (textPosition == ITextPositionCapability.UNKNOWN_POSITION) {
                    textPosition = getDefaultTextPosition(selectItem);
                }

                if (textPosition > 0) {
                    ((ITextPositionCapability) itemComponent)
                            .setTextPosition(textPosition);
                }
            }
        }

        if (selectItem instanceof IVisibleItem) {

            ItemsListComponent itemsListComponent = (ItemsListComponent) getComponent();

            int hiddenMode = IHiddenModeCapability.IGNORE_HIDDEN_MODE;

            if (itemComponent instanceof IHiddenModeCapability) {
                if (itemsListComponent.isItemHiddenModeSetted()) {
                    hiddenMode = itemsListComponent
                            .getItemHiddenMode(getComponentRenderContext()
                                    .getFacesContext());
                }

                if (hiddenMode == 0) {
                    hiddenMode = IHiddenModeCapability.IGNORE_HIDDEN_MODE;
                }

                ((IHiddenModeCapability) itemComponent)
                        .setHiddenMode(hiddenMode);
            }

            if (((IVisibleItem) selectItem).isVisible() == false) {

                // if the component is not to be drawn on the client :
                // exit
                if (hiddenMode == IHiddenModeCapability.SERVER_HIDDEN_MODE) {
                    return SKIP_NODE;
                }

                if (itemComponent instanceof IVisibilityCapability) {
                    IVisibilityCapability visibilityCapability = (IVisibilityCapability) itemComponent;

                    visibilityCapability.setVisible(false);
                }
            }
        }

        if (selectItem instanceof IAccessKeyItem) {
            String accessKey = ((IAccessKeyItem) selectItem).getAccessKey();

            if (accessKey != null
                    && (itemComponent instanceof IAccessKeyCapability)) {
                ((IAccessKeyCapability) itemComponent).setAccessKey(accessKey);
            }
        }

        String description = selectItem.getDescription();
        if (description != null
                && (itemComponent instanceof IToolTipTextCapability)) {
            ((IToolTipTextCapability) itemComponent).setToolTipText(description);
        }

        boolean disabled = selectItem.isDisabled();
        if (disabled && (itemComponent instanceof IDisabledCapability)) {
            ((IDisabledCapability) itemComponent).setDisabled(true);
        }

        if (itemComponent instanceof ILookAndFeelCapability) {
            String lookId = null;

            if (selectItem instanceof ILookAndFeelItem) {
                ILookAndFeelItem lookIdItem = (ILookAndFeelItem) selectItem;

                lookId = lookIdItem.getLookId();
            }

            if (lookId == null) {
                lookId = defaultItemLookId;
            }

            if (lookId != null) {
                ((ILookAndFeelCapability) itemComponent).setLookId(lookId);
            }
        }

        if (itemComponent instanceof IStyleClassCapability) {
            String cssClass = null;
            if (selectItem instanceof IStyleClassItem) {
                IStyleClassItem lookIdItem = (IStyleClassItem) selectItem;

                cssClass = lookIdItem.getStyleClass();
            }
            if (cssClass == null) {
                cssClass = defaultItemStyleClass;
            }

            if (cssClass != null) {
                ((IStyleClassCapability) itemComponent).setStyleClass(cssClass);
            }
        }

        if (selectItem instanceof IAccessKeyItem) {
            IAccessKeyItem accessKeyItem = (IAccessKeyItem) selectItem;

            String accessKey = accessKeyItem.getAccessKey();
            if (accessKey != null
                    && (itemComponent instanceof IAccessKeyCapability)) {

                ((IAccessKeyCapability) itemComponent).setAccessKey(accessKey);
            }
        }

        if (selectItem instanceof IBorderTypeItem) {
            IBorderTypeItem borderTypeItem = (IBorderTypeItem) selectItem;

            String borderType = borderTypeItem.getBorderType();
            if (borderType != null
                    && (itemComponent instanceof IBorderTypeCapability)) {

                ((IBorderTypeCapability) itemComponent)
                        .setBorderType(borderType);
            }
        }

        if (selectItem instanceof IImagesItem) {
            IImagesItem imagesSelectItem = (IImagesItem) selectItem;

            if (itemComponent instanceof IImageCapability) {
                String imageURL = imagesSelectItem.getImageURL();
                if (imageURL == null && defaultImageURL != null) {
                    imageURL = defaultImageURL;
                }
                if (imageURL != null) {
                    ((IImageCapability) itemComponent).setImageURL(imageURL);
                }
            }
            if (itemComponent instanceof IStatesImageCapability) {
                IStatesImageCapability is = (IStatesImageCapability) itemComponent;

                String disabledImageURL = imagesSelectItem
                        .getDisabledImageURL();
                if (disabledImageURL == null) {
                    disabledImageURL = defaultDisabledImageURL;
                }
                if (disabledImageURL != null) {
                    is.setDisabledImageURL(disabledImageURL);
                }

                String hoverImageURL = imagesSelectItem.getHoverImageURL();
                if (hoverImageURL == null) {
                    hoverImageURL = defaultHoverImageURL;
                }
                if (hoverImageURL != null) {
                    is.setHoverImageURL(hoverImageURL);
                }

                String selectedImageURL = imagesSelectItem
                        .getSelectedImageURL();
                if (selectedImageURL == null) {
                    selectedImageURL = defaultSelectedImageURL;
                }
                if (selectedImageURL != null) {
                    is.setSelectedImageURL(selectedImageURL);
                }

                String expandImageURL = imagesSelectItem.getExpandedImageURL();
                if (expandImageURL != null
                        && (itemComponent instanceof IExpandImageCapability)) {
                    ((IExpandImageCapability) itemComponent)
                            .setExpandedImageURL(expandImageURL);
                }
            }
        } else {
            if (itemComponent instanceof IImageCapability) {
                if (defaultImageURL != null) {
                    ((IImageCapability) itemComponent)
                            .setImageURL(defaultImageURL);
                }
            }
            if (itemComponent instanceof IStatesImageCapability) {
                IStatesImageCapability sic = (IStatesImageCapability) itemComponent;
                if (defaultDisabledImageURL != null) {
                    sic.setDisabledImageURL(defaultDisabledImageURL);
                }
                if (defaultHoverImageURL != null) {
                    sic.setHoverImageURL(defaultHoverImageURL);
                }
                if (defaultSelectedImageURL != null) {
                    sic.setSelectedImageURL(defaultSelectedImageURL);
                }
            }
        }

        if (itemComponent instanceof IImageSizeCapability) {
            IImageSizeCapability isc = (IImageSizeCapability) itemComponent;

            int width = 0;
            int height = 0;

            if (selectItem instanceof IImageSizeItem) {
                IImageSizeItem ss = (IImageSizeItem) selectItem;

                width = ss.getImageWidth();
                height = ss.getImageHeight();
            }

            if (width < 1 || height < 1) {
                width = getToolItemImageDefaultWidth(selectItem);
                height = getToolItemImageDefaultHeight(selectItem);
            }

            if (width > 0) {
                isc.setImageWidth(width);
            }
            if (height > 0) {
                isc.setImageHeight(height);
            }
        }

        if (itemComponent instanceof ISizeCapability) {
            ISizeCapability isc = (ISizeCapability) itemComponent;

            String width = "";

            if (selectItem instanceof IWidthItem) {
                IWidthItem iwi = (IWidthItem) selectItem;

                width = iwi.getWidth();
            }

            if (width != null && width.length() > 0) {
                isc.setWidth(width);
            }
        }

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        Renderer renderer = getRenderer(facesContext, itemComponent);
        if (renderer == null) {
            LOG.error("No renderer for component '" + itemComponent + "' ?");
            return EVAL_NODE;
        }

        encodeItem(renderer, component, itemComponent, itemId);

        return EVAL_NODE;
    }

    /**
     * Ideal placeholder for modifying stuff at generation time have a look at
     * preEncodeItem and postEncodeItem
     * 
     * @param renderer
     * @param component
     * @param itemComponent
     * @param itemId
     * @throws WriterException
     */
    protected void encodeItem(Renderer renderer, UIComponent component,
            UIComponent itemComponent, String itemId) throws WriterException {

        preEncodeItem(renderer, component, itemComponent, itemId);

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        htmlWriter.startElement(IHtmlWriter.LI);
        htmlWriter.writeClass("f_itemsList_item");

        if (itemPaddingSetted && itemPadding >= 0) {
            htmlWriter.writeStyle().writePadding(itemPadding + "px");
        }

        htmlWriter.endComponent();

        List<UIComponent> children = component.getChildren();

        try {
            children.add(itemComponent);

            try {
                renderer.encodeBegin(facesContext, itemComponent);

                renderer.encodeEnd(facesContext, itemComponent);

            } catch (IOException ex) {
                throw new WriterException(ex.getMessage(), ex.getCause(),
                        component);
            }

            String itemClientId = itemComponent.getClientId(facesContext);

            itemIdToClientId.put(itemId, itemClientId);

        } finally {
            children.remove(itemComponent);
        }

        htmlWriter.endElement(IHtmlWriter.LI);

        postEncodeItem(renderer, component, itemComponent, itemId);
    }

    /**
     * called before the encodeItem
     * 
     * @param renderer
     * @param component
     * @param itemComponent
     * @param itemId
     */
    protected void preEncodeItem(Renderer renderer, UIComponent component,
            UIComponent itemComponent, String itemId) throws WriterException {
        // called before the encodeItem

    }

    /**
     * called after the encodeItem
     * 
     * @param renderer
     * @param component
     * @param itemComponent
     * @param itemId
     */
    protected void postEncodeItem(Renderer renderer, UIComponent component,
            UIComponent itemComponent, String itemId) throws WriterException {
        // called after theencodeItem

    }

    /**
     * text position
     * 
     * @param selectItem
     * @return cf. {@link ITextPositionCapability}
     */
    private int getDefaultTextPosition(SelectItem selectItem) {
        UIComponent component = getComponent();

        if (component instanceof ITextPositionCapability) {
            return ((ITextPositionCapability) component).getTextPosition();
        }

        return 0;
    }

    /**
     * 
     * @param selectItem
     * @return default height
     */
    protected int getToolItemImageDefaultHeight(SelectItem selectItem) {
        return -1;
    }

    /**
     * 
     * @param selectItem
     * @return default width
     */
    protected int getToolItemImageDefaultWidth(SelectItem selectItem) {
        return -1;
    }

    /**
     * 
     * @param facesContext
     * @param itemComponent
     * @return renderer
     */
    private Renderer getRenderer(FacesContext facesContext,
            UIComponent itemComponent) {
        String rendererType = itemComponent.getRendererType();
        if (rendererType == null) {
            return null;
        }

        RenderKitFactory rkFactory = (RenderKitFactory) FactoryFinder
                .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = rkFactory.getRenderKit(facesContext, facesContext
                .getViewRoot().getRenderKitId());

        return renderKit.getRenderer(itemComponent.getFamily(), rendererType);

    }

    /**
     * @param component
     *            component item
     * @return {@link SelectItem}
     */
    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof ToolItemComponent) {
            return new ToolItem((ToolItemComponent) component);
        }

        return super.createSelectItem(component);
    }

    /**
     * @param component
     *            component
     * @return {@link SelectItem}
     */
    protected SelectItem getUnknownComponent(UIComponent component) {
        if (component instanceof ToolItemSeparatorComponent) {
            return SeparatorSelectItem.SEPARATOR;
        }

        return super.getUnknownComponent(component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#createContext
     * (org.rcfaces.core.internal.renderkit.IWriter)
     */
    protected SelectItemsContext createHtmlContext() {
        IComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        ItemsListComponent itemsListComponent = (ItemsListComponent) componentRenderContext
                .getComponent();

        return new ToolBarContext(this, componentRenderContext,
                itemsListComponent.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#createContext
     * (org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter)
     */
    protected SelectItemsContext createJavaScriptContext() {

        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        ItemsListComponent itemsListComponent = (ItemsListComponent) componentRenderContext
                .getComponent();

        return new ToolBarContext(this, componentRenderContext,
                itemsListComponent.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.AbstractCameliaRenderer#decode(javax
     * .faces.component.UIComponent,
     * org.rcfaces.core.internal.renderkit.IComponentData)
     */
    public void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {

        super.decode(context, component, componentData);

        // ItemsListComponent itemsListComponent =
        // (ItemsListComponent) component;

        Map childrenClientIds = mapChildrenClientId(null, context, component);

        String disabledItems = componentData.getStringProperty(DISABLED_ITEMS);
        if (disabledItems != null && disabledItems.length() > 0) {
            List l = listComponents(childrenClientIds, disabledItems,
                    IDisabledCapability.class);

            for (Iterator it = l.iterator(); it.hasNext();) {
                IDisabledCapability disabledCapability = (IDisabledCapability) it
                        .next();

                disabledCapability.setDisabled(true);
            }
        }

        String enabledItems = componentData.getStringProperty(ENABLED_ITEMS);
        if (enabledItems != null && enabledItems.length() > 0) {
            List l = listComponents(childrenClientIds, enabledItems,
                    IDisabledCapability.class);

            for (Iterator it = l.iterator(); it.hasNext();) {
                IDisabledCapability disabledCapability = (IDisabledCapability) it
                        .next();

                disabledCapability.setDisabled(false);
            }
        }

        if (componentData.isEventComponent() == false) {
            // Si il n'y a pas d'evenement Camelia, on regarde les evenements
            // HTML !

            String eventComponentId = ((IHtmlRequestContext) context)
                    .getEventComponentId();

            Map componentIdToValue = (Map) getComponent().getAttributes().get(
                    COMPONENT_ID_TO_VALUE_PROPERTY);
            if (componentIdToValue != null) {
                for (Iterator it = componentIdToValue.entrySet().iterator(); it
                        .hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();

                    String clientId = (String) entry.getKey();

                    if (clientId.equals(eventComponentId) == false) {
                        continue;
                    }

                    String selectItemValue = (String) entry.getValue();

                    Object value = convertToItemValue(
                            context.getFacesContext(), component,
                            selectItemValue);

                    ActionEvent actionEvent = new SelectionEvent(component,
                            selectItemValue, value, null, 0);

                    actionEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
                    component.queueEvent(actionEvent);

                    break;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.renderkit.html.internal.decorator.AbstractSelectItemsDecorator
     * #convertToSelectItem(java.lang.Object)
     */
    protected SelectItem convertToSelectItem(Object value) {
        if (value instanceof IToolItem) {
            IToolItem toolItem = (IToolItem) value;

            return new ToolItem(toolItem);
        }

        return super.convertToSelectItem(value);
    }

    /**
     * @return next
     */
    public String nextItemId() {
        return itemsId.remove(0);
    }

    /**
     * 
     * @return new id
     */
    public String allocateItemId() {
        String id = "_item" + itemsId.size();

        itemsId.add(id);

        return id;
    }

    /**
     * 
     */
    public void allocateItemSeparator() {

        itemsId.add(null);
    }

    /**
     * menu (not used yet)
     * 
     * @param selectItemVarName
     * @param selectItemComponentId
     * @param javascriptWriter
     * @throws WriterException
     */
    public ItemsMenuDecorator pushMenuDecorator(String selectItemVarName,
            String selectItemComponentId, IJavaScriptWriter javascriptWriter)
            throws WriterException {

        ItemsMenuDecorator itemsMenuDecorator = new ItemsMenuDecorator(
                javascriptWriter.getComponentRenderContext().getComponent(),
                selectItemComponentId, selectItemVarName);

        itemsMenuDecorator.initializeItemContext(javascriptWriter);

        menuDecoratorStack.add(itemsMenuDecorator);

        return itemsMenuDecorator;
    }

    /**
     * menu (not used yet)
     * 
     */
    public void popupMenuDecorator() {

        ItemsMenuDecorator itemsMenuDecorator = menuDecoratorStack
                .remove(menuDecoratorStack.size() - 1);

        itemsMenuDecorator.finalizeItemContext();
    }

    /**
     * menu (not used yet)
     */
    public ItemsMenuDecorator peekMenuDecorator() {
        return menuDecoratorStack.get(menuDecoratorStack.size() - 1);
    }
}
