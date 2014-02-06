/*
 * $Id: MenuBarDecorator.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.item.IAccessKeyItem;
import org.rcfaces.core.item.IClientDataItem;
import org.rcfaces.core.item.IImagesItem;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class MenuBarDecorator extends MenuDecorator {

    

    public MenuBarDecorator(UIComponent component) {
        super(component);
    }

    protected void encodeJsMenuItemBegin(UIComponent component,
            SelectItem selectItem, boolean hasChild) throws WriterException {

        if (getContext().getDepth() == 1) {
            encodeJsMenuBarItemBegin(component, selectItem, hasChild);
            return;
        }

        super.encodeJsMenuItemBegin(component, selectItem, hasChild);
    }

    protected void encodeJsMenuBarItemBegin(UIComponent component,
            SelectItem selectItem, boolean hasChild) throws WriterException {

        IComponentRenderContext componentContext = javaScriptWriter
                .getHtmlComponentRenderContext();

        String varId = javaScriptWriter.getJavaScriptRenderContext()
                .allocateVarName();

        MenuContext menuContext = getMenuContext();

        menuContext.pushVarId(varId);

        String sid = menuContext.getComponentClientId(component); // menuContext.getMenuBarItemId();

        javaScriptWriter.write("var ").write(varId).write('=').writeMethodCall(
                "f_declareBarItem2").writeString(sid);

        menuContext.setManagerComponentId(javaScriptWriter
                .getComponentVarName());

        javaScriptWriter.write(',');

        IObjectLiteralWriter objectLiteralWriter = javaScriptWriter
                .writeObjectLiteral(false);

        String value = convertItemValue(componentContext, selectItem.getValue());
        objectLiteralWriter.writeSymbol("_value").writeString(value);

        String txt = selectItem.getLabel();
        if (txt != null) {
            objectLiteralWriter.writeSymbol("_label").writeString(txt);
        }

        boolean disabled = selectItem.isDisabled();
        if (disabled) {
            objectLiteralWriter.writeSymbol("_disabled").writeBoolean(true);
        }

        if (selectItem instanceof IAccessKeyItem) {
            String key = ((IAccessKeyItem) selectItem).getAccessKey();
            if (key != null && key.length() > 0) {
                objectLiteralWriter.writeSymbol("_accessKey").writeString(key);
            }
        }

        if (selectItem instanceof IStyleClassItem) {
            String styleClass = ((IStyleClassItem) selectItem).getStyleClass();
            if (styleClass != null) {
                objectLiteralWriter.writeSymbol("_styleClass").writeString(
                        styleClass);
            }
        }

        if (selectItem instanceof IImagesItem) {
            writeSelectItemImages((IImagesItem) selectItem, javaScriptWriter,
                    null, null, true, objectLiteralWriter);
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

    protected void encodeJsMenuItemEnd(UIComponent component,
            SelectItem selectItem, boolean hasChild) {

        if (getContext().getDepth() == 1) {
            encodeJsMenuBarItemEnd(component, selectItem, hasChild);
            return;
        }

        super.encodeJsMenuItemEnd(component, selectItem, hasChild);
    }

    protected void encodeJsMenuBarItemEnd(UIComponent component,
            SelectItem selectItem, boolean hasChild) {

        // IWriter writer = context.getWriter();

        if (hasChild) {
            encodeJsMenuPopupEnd();
        }

        getMenuContext().popVarId();
    }

}
