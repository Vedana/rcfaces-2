/*
 * $Id: ItemsMenuDecorator.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;

import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class ItemsMenuDecorator extends MenuDecorator {
    

    private static final String MENU_POPUPID = "popup";

    protected final String menuId;

    private String itemId;

    private final String selectItemVarName;

    private boolean removeAllWhenShown;

    private int itemImageWidth;

    private int itemImageHeight;

    public ItemsMenuDecorator(UIComponent component, String itemId,
            String selectItemVarName) {
        super(component);

        this.itemId = itemId;
        this.menuId = "#popup";
        this.selectItemVarName = selectItemVarName;
        // this.removeAllWhenShown = removeAllWhenShown;
        // this.itemImageWidth = itemImageWidth;
        // this.itemImageHeight = itemImageHeight;
    }

    protected SelectItemsContext createJavaScriptContext()
            throws WriterException {
        SelectItemsJsContext context = (SelectItemsJsContext) super
                .createJavaScriptContext();

        String menuVarName = javaScriptWriter.getJavaScriptRenderContext()
                .allocateVarName();

        javaScriptWriter.write("var ").write(menuVarName).write("=").writeCall(
                selectItemVarName, "f_newSubMenu").writeString(menuId);

        String id = context.getComponentClientId(getComponent());

        if (Constants.CLIENT_NAMING_SEPARATOR_SUPPORT) {
            IProcessContext processContext = getComponentRenderContext()
                    .getRenderContext().getProcessContext();

            String namingSeparator = processContext.getNamingSeparator();
            if (namingSeparator != null) {
                id += namingSeparator + itemId + namingSeparator + MENU_POPUPID;

            } else {
                id += NamingContainer.SEPARATOR_CHAR + itemId
                        + NamingContainer.SEPARATOR_CHAR + MENU_POPUPID;
            }
        } else {
            id += NamingContainer.SEPARATOR_CHAR + itemId
                    + NamingContainer.SEPARATOR_CHAR + MENU_POPUPID;
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

    public void initializeItemContext(IJavaScriptWriter javascriptWriter)
            throws WriterException {
        this.javaScriptWriter = javascriptWriter;
        this.selectItemsContext = createJavaScriptContext();
    }

    public void finalizeItemContext() {
        this.javaScriptWriter = null;
        this.selectItemsContext = null;
    }

}
