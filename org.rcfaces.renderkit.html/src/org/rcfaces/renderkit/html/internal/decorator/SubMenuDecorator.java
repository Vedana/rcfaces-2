/*
 * $Id: SubMenuDecorator.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesListener;

import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.EventsRenderer;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.util.ListenerTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */
public class SubMenuDecorator extends MenuDecorator {
    

    protected final String menuId;

    protected String menuVarName;

    private String suffixMenuId;

    private boolean removeAllWhenShown;

    private int itemImageWidth;

    private int itemImageHeight;

    public SubMenuDecorator(UIComponent component, String menuId,
            String suffixMenuId, boolean removeAllWhenShown,
            int itemImageWidth, int itemImageHeight) {
        super(component);

        this.suffixMenuId = suffixMenuId;
        this.menuId = menuId;
        this.removeAllWhenShown = removeAllWhenShown;
        this.itemImageWidth = itemImageWidth;
        this.itemImageHeight = itemImageHeight;
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {
        SelectItemsJsContext context = (SelectItemsJsContext) super
                .createJavaScriptContext();

        menuVarName = javaScriptWriter.getJavaScriptRenderContext()
                .allocateVarName();

        javaScriptWriter.write("var ").write(menuVarName).write("=")
                .writeMethodCall("f_newSubMenu").writeString(menuId);

        String id = context.getComponentClientId(getComponent());

        if (suffixMenuId != null) {

            if (Constants.CLIENT_NAMING_SEPARATOR_SUPPORT) {
                IProcessContext processContext = getComponentRenderContext()
                        .getRenderContext().getProcessContext();

                String namingSeparator = processContext.getNamingSeparator();
                if (namingSeparator != null) {
                    id += namingSeparator + suffixMenuId;

                } else {
                    id += NamingContainer.SEPARATOR_CHAR + suffixMenuId;
                }
            } else {
                id += NamingContainer.SEPARATOR_CHAR + suffixMenuId;
            }
        }

        IObjectLiteralWriter objectLiteralWriter = javaScriptWriter.write(',')
                .writeObjectLiteral(true);

        if (id != null) {
            objectLiteralWriter.writeSymbol("_id").writeString(id);
        }

        if (removeAllWhenShown) {
            objectLiteralWriter.writeSymbol("_removeAllWhenShown")
                    .writeBoolean(true);
        }

        if (itemImageWidth > 0) {
            objectLiteralWriter.writeSymbol("_itemImageWidth").writeInt(
                    itemImageWidth);
        }

        if (itemImageHeight > 0) {
            objectLiteralWriter.writeSymbol("_itemImageHeight").writeInt(
                    itemImageHeight);
        }

        objectLiteralWriter.end().writeln(");");

        context.pushVarId(menuVarName);

        context.setManagerComponentId(menuVarName);

        return context;
    }

    protected void encodeComponentsBegin() throws WriterException {
        if (javaScriptWriter != null) {
            if (suffixMenuId == null) {
                SelectItemsJsContext context = (SelectItemsJsContext) getContext();

                Map<String, FacesListener[]> listenersByType = ListenerTools
                        .getListenersByType(
                        ListenerTools.JAVASCRIPT_NAME_SPACE, component);

                EventsRenderer.encodeEventListeners(javaScriptWriter,
                        context.peekVarId(), listenersByType, null);
            }

        }
        super.encodeComponentsBegin();
    }
}
