/*
 * $Id: ToolBarRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ItemsToolFolderComponent;
import org.rcfaces.core.component.ToolBarComponent;
import org.rcfaces.core.component.ToolFolderComponent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
@XhtmlNSAttributes({ "locked" })
public class ToolBarRenderer extends AbstractCssRenderer {

    private static final Log LOG = LogFactory.getLog(ToolBarRenderer.class);

    public static final int DEFAULT_TOOL_ITEM_SEPARATOR_WIDTH = 2;

    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ToolBarComponent toolBarComponent = (ToolBarComponent) componentRenderContext
                .getComponent();

        htmlWriter.startElement(IHtmlWriter.DIV);
        // htmlWriter.writeCellPadding(0);
        // htmlWriter.writeCellSpacing(0);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        String verticalAlignment = toolBarComponent
                .getVerticalAlignment(facesContext);
        if (verticalAlignment != null) {
            htmlWriter.writeVAlign(verticalAlignment);
        }

        if (toolBarComponent.isLocked(facesContext)) {
            htmlWriter.writeAttributeNS("locked", true);
        }
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        // htmlWriter.startElement(IHtmlWriter.P);
        // htmlWriter.write("&nbsp;");
        // htmlWriter.endElement(IHtmlWriter.P);

        htmlWriter.endElement(IHtmlWriter.DIV);

        // Pour masquer les separateurs
        htmlWriter.getJavaScriptEnableMode().enableOnInit();

        super.encodeEnd(writer);
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {

        List children = component.getChildren();

        for (Iterator it = children.iterator(); it.hasNext();) {
            UIComponent child = (UIComponent) it.next();

            if (child instanceof ItemsToolFolderComponent) {
                encodeToolFolder(facesContext, child);
                continue;
            }
            if (child instanceof ToolFolderComponent) {
                encodeToolFolder(facesContext, child);
                continue;
            }

            LOG.error("Invalid child of ToolBar: " + component + " id="
                    + component.getId());
        }
    }

    protected void encodeToolFolder(FacesContext facesContext,
            UIComponent component) throws WriterException {

        Renderer renderer = getRenderer(facesContext, component);

        if (renderer == null) {
            LOG.error("Can not get renderer for component '" + component
                    + "' id=" + component.getId());
            return;
        }

        // IRenderContext renderContext = getRenderContext(facesContext);

        // IHtmlWriter htmlWriter = (IHtmlWriter)
        // renderContext.getComponentWriter();

        // htmlWriter.startElement(IHtmlWriter.LI);
        // htmlWriter.writeClass("f_toolBar_item");

        ComponentTools.encodeRecursive(facesContext, component);

        // htmlWriter.endElement(IHtmlWriter.LI);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TOOL_BAR;
    }

    public boolean getDecodesChildren() {
        return true;
    }
}