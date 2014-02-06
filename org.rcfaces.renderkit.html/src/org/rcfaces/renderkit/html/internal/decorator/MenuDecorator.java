/*
 * $Id: MenuDecorator.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
import javax.faces.model.SelectItem;

import org.rcfaces.core.component.MenuItemComponent;
import org.rcfaces.core.component.MenuSeparatorComponent;
import org.rcfaces.core.component.capability.IDisabledCapability;
import org.rcfaces.core.component.capability.IInputTypeCapability;
import org.rcfaces.core.internal.capability.ICheckComponent;
import org.rcfaces.core.internal.listener.IScriptListener;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.CheckTools;
import org.rcfaces.core.internal.util.KeyTools;
import org.rcfaces.core.item.IAcceleratorKeyItem;
import org.rcfaces.core.item.IAccessKeyItem;
import org.rcfaces.core.item.ICheckSelectItem;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IGroupSelectItem;
import org.rcfaces.core.item.IImagesItem;
import org.rcfaces.core.item.IInputTypeItem;
import org.rcfaces.core.item.IMenuItem;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.core.item.IVisibleItem;
import org.rcfaces.core.item.MenuItem;
import org.rcfaces.core.item.SeparatorSelectItem;
import org.rcfaces.renderkit.html.internal.EventsRenderer;
import org.rcfaces.renderkit.html.internal.HtmlValuesTools;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */
public class MenuDecorator extends AbstractSelectItemsDecorator {
    

    private static final String DISABLED_ITEMS = "disabledItems";

    private static final String ENABLED_ITEMS = "enabledItems";

    private static final String CHECKED_ITEMS = "checkedItems";

    private static final String UNCHECKED_ITEMS = "uncheckedItems";

    private final Set<String> subClientIds = new HashSet<String>();

    public MenuDecorator(UIComponent component) {
        super(component, null);
    }

    protected void preEncodeContainer() throws WriterException {

        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        super.preEncodeContainer();
    }

    /*
     * // enableJavaScript(writer); // writeHtmlAttributes(writer); //
     * writeJavaScriptAttributes(writer); // writeCssAttributes(writer);
     */

    protected void postEncodeContainer() throws WriterException {
        super.postEncodeContainer();

        // writer.endElement(IHtmlWriter.DIV");
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#
     * encodeTreeNodeBegin
     * (org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer
     * .TreeContext, javax.faces.component.UIComponent,
     * javax.faces.model.SelectItem)
     */
    public int encodeNodeBegin(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (javaScriptWriter == null) {
            // Rendu HTML !
            /*
             * if (context.getDepth() == 0) {
             * encodeHtmlMenuBarItemBegin((MenuBarContext) context, component,
             * selectItem, hasChild); return; }
             */
            return SKIP_NODE;
        }

        if (SeparatorSelectItem.isSeparator(selectItem)) {
            encodeJsMenuItemSeparator(component);
            return EVAL_NODE;
        }

        encodeJsMenuItemBegin(component, selectItem, hasChild);

        return EVAL_NODE;
    }

    protected void encodeJsMenuItemBegin(UIComponent component,
            SelectItem selectItem, boolean hasChild) throws WriterException {

        MenuContext menuContext = getMenuContext();

        String managerVarId = menuContext.getManagerVarId();
        String parentVarId = menuContext.peekVarId();

        String varId = javaScriptWriter.getJavaScriptRenderContext()
                .allocateVarName();
        // String varId = "m" + vid;
        menuContext.pushVarId(varId);

        String sid = menuContext.getComponentClientId(component);
        if (subClientIds.add(sid) == false) {
            sid += "::" + subClientIds.size();
            subClientIds.add(sid);
        }

        int style = IInputTypeCapability.AS_PUSH_BUTTON;
        if (selectItem instanceof IInputTypeItem) {
            style = ((IInputTypeItem) selectItem).getInputType();

        } else if (selectItem instanceof IGroupSelectItem) {
            style = IInputTypeCapability.AS_RADIO_BUTTON;

        } else if (selectItem instanceof ICheckSelectItem) {
            style = IInputTypeCapability.AS_CHECK_BUTTON;
        }

        String groupName = null;
        if (style == IInputTypeCapability.AS_RADIO_BUTTON) {
            groupName = ((IGroupSelectItem) selectItem).getGroupName();
            if (groupName != null) {
                groupName = javaScriptWriter.allocateString(groupName);
            }
        }

        String jsStyleClass = null;
        if (selectItem instanceof IStyleClassItem) {
            String styleClass = ((IStyleClassItem) selectItem).getStyleClass();
            if (styleClass != null) {
                jsStyleClass = javaScriptWriter.allocateString(styleClass);
            }
        }

        String jsDescription = selectItem.getDescription();
        if (jsDescription != null) {
            jsDescription = javaScriptWriter.allocateString(jsDescription);
        }

        IComponentRenderContext componentContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        Object selectItemValue = selectItem.getValue();
        String value = convertItemValue(componentContext, selectItemValue);

        /*
         * Finalement en laisse en String !
         * 
         * if (value != null) { value = javaScriptWriter.allocateString(value);
         * }
         */

        javaScriptWriter.write("var ").write(varId).write('=');

        javaScriptWriter.writeCall(managerVarId, "f_appendItem2");
        javaScriptWriter.write(parentVarId).write(',').writeString(sid).write(
                ',');

        IObjectLiteralWriter objectLiteralWriter = javaScriptWriter
                .writeObjectLiteral(false);

        objectLiteralWriter.writeSymbol("_value").writeString(value);

        String txt = selectItem.getLabel();
        if (txt != null) {
            objectLiteralWriter.writeSymbol("_label").writeString(txt);
        }

        if (jsDescription != null) {
            objectLiteralWriter.writeSymbol("_description")
                    .write(jsDescription);
        }

        boolean disabled = selectItem.isDisabled();
        if (disabled) {
            objectLiteralWriter.writeSymbol("_disabled").writeBoolean(true);
        }

        switch (style) {
        case IInputTypeCapability.AS_RADIO_BUTTON:
            if (groupName != null) {
                objectLiteralWriter.writeSymbol("_groupName").write(groupName);
            }
            // On continue

        case IInputTypeCapability.AS_CHECK_BUTTON:
            objectLiteralWriter.writeSymbol("_type").writeInt(style);
            
            if (menuContext.isValueChecked(selectItem, selectItemValue)) {
                objectLiteralWriter.writeSymbol("_checked").writeBoolean(true);
            }

            break;
        }
        
        if (this instanceof CriteriaMenuDecorator) {
        	objectLiteralWriter.writeSymbol("_criteriaPopup").writeBoolean(true);
        }

        if (selectItem instanceof IAccessKeyItem) {
            String key = ((IAccessKeyItem) selectItem).getAccessKey();
            if (key != null && key.length() > 0) {
                objectLiteralWriter.writeSymbol("_accessKey").writeString(key);
            }
        }

        if (selectItem instanceof IVisibleItem) {
            boolean visible = ((IVisibleItem) selectItem).isVisible();
            if (visible == false) {
                objectLiteralWriter.writeSymbol("_visible").writeBoolean(false);
            }
        }

        if (jsStyleClass != null) {
            objectLiteralWriter.writeSymbol("_styleClass").write(jsStyleClass);
        }

        if (selectItem instanceof IAcceleratorKeyItem) {
            String acceleratorKey = ((IAcceleratorKeyItem) selectItem)
                    .getAcceleratorKey();
            if (acceleratorKey != null && acceleratorKey.length() > 0) {
                KeyTools.State state = KeyTools.parseKeyBinding(acceleratorKey);
                objectLiteralWriter.writeSymbol("_acceleratorKey").writeString(
                        state.format());
            }
        }

        if (selectItem instanceof IImagesItem) {
            writeSelectItemImages((IImagesItem) selectItem, javaScriptWriter,
                    null, null, true, objectLiteralWriter);
        }

        MenuItemComponent menuItemComponent = null;
        if (component instanceof MenuItemComponent) {
            menuItemComponent = (MenuItemComponent) component;

            FacesContext facesContext = javaScriptWriter.getFacesContext();
            FacesListener facesListeners[] = menuItemComponent
                    .listMenuListeners();

            IProcessContext processContext = javaScriptWriter
                    .getHtmlRenderContext().getProcessContext();
            List<IScriptListener> l = null;
            for (int i = 0; i < facesListeners.length; i++) {
                FacesListener facesListener = facesListeners[i];

                if ((facesListener instanceof IScriptListener) == false) {
                    continue;
                }

                IScriptListener scriptListener = (IScriptListener) facesListener;

                if (IHtmlRenderContext.JAVASCRIPT_TYPE.equals(scriptListener
                        .getScriptType(processContext)) == false) {
                    continue;
                }
                if (l == null) {
                    l = new ArrayList<IScriptListener>(facesListeners.length
                            - i);
                }

                l.add(scriptListener);
            }

            boolean removeAllWhenShow = menuItemComponent
                    .isRemoveAllWhenShown(facesContext);
            if (removeAllWhenShow) {
                objectLiteralWriter.writeSymbol("_removeAllWhenShow")
                        .writeBoolean(true);
            }

            if (l != null) {
                objectLiteralWriter.writeSymbol("_menuListeners").write('[');

                boolean first = true;
                for (Iterator it = l.iterator(); it.hasNext();) {
                    IScriptListener scriptListener = (IScriptListener) it
                            .next();

                    if (first == false) {
                        javaScriptWriter.write(',');
                    } else {
                        first = false;
                    }

                    EventsRenderer.encodeJavaScriptCommmand(javaScriptWriter,
                            scriptListener);
                }
                javaScriptWriter.write(']');
            }
        }

        if (selectItem instanceof IClientDataItem) {
            writeItemClientDatas((IClientDataItem) selectItem,
                    javaScriptWriter, null, null, objectLiteralWriter);
        }

        objectLiteralWriter.end().writeln(");");

        if (hasChild == false) {
            return;
        }

        encodeJsMenuPopupBegin(sid);
    }

    protected final MenuContext getMenuContext() {
        return (MenuContext) getContext();
    }

    protected void encodeJsMenuPopupBegin(String itemId) {
        // IWriter writer = context.getWriter();
    }

    protected void encodeJsMenuPopupEnd() {
        // IWriter writer = context.getWriter();

    }

    protected void encodeJsMenuItemSeparator(UIComponent component)
            throws WriterException {

        MenuContext menuContext = getMenuContext();
        String parentVarId = menuContext.peekVarId();
        String managerVarId = menuContext.getManagerVarId();

        javaScriptWriter.writeCall(managerVarId, "f_appendSeparatorItem")
                .write(parentVarId).writeln(");");
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.SelectItemsRenderer#
     * encodeTreeNodeEnd
     * (org.rcfaces.core.internal.renderkit.html.SelectItemsRenderer
     * .TreeContext, javax.faces.component.UIComponent,
     * javax.faces.model.SelectItem)
     */
    public void encodeNodeEnd(UIComponent component, SelectItem selectItem,
            boolean hasChild, boolean isVisible) throws WriterException {

        if (javaScriptWriter == null) {
            // Rendu HTML !

            return;
        }

        if (SeparatorSelectItem.isSeparator(selectItem)) {
            return;
        }

        encodeJsMenuItemEnd(component, selectItem, hasChild);
    }

    protected void encodeJsMenuItemEnd(UIComponent component,
            SelectItem selectItem, boolean hasChild) {

        // IWriter writer = context.getWriter();

        if (hasChild) {
            encodeJsMenuPopupEnd();
        }

        getMenuContext().popVarId();
    }

    protected SelectItem getUnknownComponent(UIComponent component) {
        if (component instanceof MenuSeparatorComponent) {
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
        return null;
        /*
         * MenuBarComponent menuBarComponent = (MenuBarComponent) writer
         * .getComponent();
         * 
         * return new MenuBarContext(this, writer, menuBarComponent.getValue());
         */
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof MenuItemComponent) {
            return new MenuItem((MenuItemComponent) component);
        }

        return super.createSelectItem(component);
    }

    protected SelectItem convertToSelectItem(Object value) {
        if (value instanceof IMenuItem) {
            IMenuItem menuItem = (IMenuItem) value;

            return new MenuItem(menuItem);
        }

        return super.convertToSelectItem(value);
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {
        IComponentRenderContext componentRenderContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        Object value = null;

        if (componentRenderContext instanceof ValueHolder) {
            value = ((ValueHolder) getComponent()).getValue();

        } else if (componentRenderContext instanceof UICommand) {
            value = ((UICommand) getComponent()).getValue();
        }

        return new MenuContext(this, componentRenderContext, getComponent(),
                value, 0);
    }

    public boolean getDecodesChildren() {
        return true;
    }

    public void decode(IRequestContext requestContext,
            UIComponent decodedComponent, IComponentData componentData) {

    	
    	String requestComponentId = requestContext.getComponentId(component);
    	
    	componentData = requestContext.getComponentData(component, requestComponentId, null);
    	
        super.decode(requestContext, decodedComponent, componentData);

        FacesContext facesContext = requestContext.getFacesContext();

        Map childrenClientIds = null;

        String disabledItems = componentData.getStringProperty(DISABLED_ITEMS);
        if (disabledItems != null && disabledItems.length() > 0) {
            if (childrenClientIds == null) {
                childrenClientIds = mapChildrenClientId(null, requestContext,
                        component);
            }

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
            if (childrenClientIds == null) {
                childrenClientIds = mapChildrenClientId(null, requestContext,
                        component);
            }

            List l = listComponents(childrenClientIds, enabledItems,
                    IDisabledCapability.class);

            for (Iterator it = l.iterator(); it.hasNext();) {
                IDisabledCapability disabledCapability = (IDisabledCapability) it
                        .next();

                disabledCapability.setDisabled(false);
            }
        }

        if (component instanceof ICheckComponent) {

            String checkedItems = componentData
                    .getStringProperty(CHECKED_ITEMS);
            String uncheckedItems = componentData
                    .getStringProperty(UNCHECKED_ITEMS);

            if (checkedItems != null || uncheckedItems != null) {
                boolean checkModified = false;
                Set<Object> checkValues = CheckTools.checkValuesToSet(
                        facesContext, (ICheckComponent) component, false);

                if (checkedItems != null && checkedItems.length() > 0) {

                    List<Object> values = HtmlValuesTools.parseValues(
                            facesContext, component, true, false, checkedItems);

                    if (checkValues.addAll(values)) {
                        checkModified = true;
                    }
                }

                if (uncheckedItems != null && uncheckedItems.length() > 0) {
                    List<Object> values = HtmlValuesTools.parseValues(
                            facesContext, component, true, false,
                            uncheckedItems);

                    if (checkValues.removeAll(values)) {
                        checkModified = true;
                    }
                }

                if (checkModified) {
                    CheckTools.setCheckValues(facesContext,
                            (ICheckComponent) component, checkValues);
                }
            }
        }
    }
}
