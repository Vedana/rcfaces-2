/*
 * $Id: ToolFolderRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.render.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.ISeparatorComponent;
import org.rcfaces.core.component.ToolBarComponent;
import org.rcfaces.core.component.ToolFolderComponent;
import org.rcfaces.core.component.capability.IAlternateTextCapability;
import org.rcfaces.core.component.capability.IBorderTypeCapability;
import org.rcfaces.core.internal.component.IToolBarImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "separator" })
public class ToolFolderRenderer extends AbstractCssRenderer {

    private static final Log LOG = LogFactory.getLog(ToolFolderRenderer.class);

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TOOL_FOLDER;
    }

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        encodeBeginToolFolder((IHtmlWriter) writer,
                (ToolFolderComponent) writer.getComponentRenderContext()
                        .getComponent());
    }

    protected void encodeBeginToolFolder(IHtmlWriter writer,
            ToolFolderComponent toolFolderComponent) throws WriterException {

        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();

        writer.startElement(IHtmlWriter.UL);

        // writer.writeCellSpacing(0);

        writeHtmlAttributes(writer);
        writeJavaScriptAttributes(writer);
        writeCssAttributes(writer);

        String verticalAlignment = toolFolderComponent
                .getVerticalAlignment(facesContext);
        if (verticalAlignment != null) {
            writer.writeVAlign(verticalAlignment);
        }
    }

    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        encodeEndToolFolder((IHtmlWriter) writer, (ToolFolderComponent) writer
                .getComponentRenderContext().getComponent());

        super.encodeEnd(writer);
    }

    protected void encodeEndToolFolder(IHtmlWriter writer,
            ToolFolderComponent component) throws WriterException {
        writer.endElement(IHtmlWriter.UL);
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {
        IHtmlRenderContext renderContext = getHtmlRenderContext(facesContext);

        IHtmlWriter htmlWriter = (IHtmlWriter) renderContext
                .getComponentWriter();

        List<UIComponent> childrenList = htmlWriter.getComponentRenderContext()
                .getComponent().getChildren();
        List<UIComponent> originalChildrenList = new ArrayList<UIComponent>(
                childrenList);

        List<UIComponent> computedChildrenList = getChildren(htmlWriter);

        for (Iterator it = computedChildrenList.iterator(); it.hasNext();) {
            UIComponent child = (UIComponent) it.next();

            encodeToolItem(htmlWriter, child);
        }

        htmlWriter.endComponent();

        childrenList.retainAll(originalChildrenList);
    }

    protected List<UIComponent> getChildren(IHtmlWriter htmlWriter) {
        return htmlWriter.getComponentRenderContext().getComponent()
                .getChildren();
    }

    protected void encodeToolItem(IHtmlWriter htmlWriter, UIComponent child)
            throws WriterException {

        ToolFolderComponent toolFolderComponent = (ToolFolderComponent) htmlWriter
                .getHtmlComponentRenderContext().getComponent();

        ToolBarComponent toolBarComponent = toolFolderComponent.getToolBar();

        if (child instanceof ISeparatorComponent) {
            encodeToolItemSeparator(htmlWriter, toolBarComponent,
                    (ISeparatorComponent) child);
            return;
        }

        FacesContext facesContext = htmlWriter.getHtmlComponentRenderContext()
                .getFacesContext();

        Renderer renderer = getRenderer(facesContext, child);

        if (renderer == null) {
            LOG.error("Can not get renderer for component '" + child + "' id="
                    + child.getId());
            return;
        }

        htmlWriter.startElement(IHtmlWriter.LI);
        htmlWriter.writeClass("f_toolFolder_item");

        if (toolBarComponent.isItemPaddingSetted()) {
            int cellPadding = toolBarComponent.getItemPadding(facesContext);
            if (cellPadding >= 0) {
                htmlWriter.writeStyle().writePadding(cellPadding + "px");
            }
        }

        if (child instanceof IBorderTypeCapability) {
            IBorderTypeCapability borderTypeCapability = (IBorderTypeCapability) child;

            if (borderTypeCapability.getBorderType() == null) {
                String borderType = null;
                if (toolFolderComponent.isBorderTypeSetted()) {
                    borderType = toolFolderComponent
                            .getBorderType(facesContext);

                } else if (toolBarComponent.isBorderTypeSetted()) {
                    borderType = toolBarComponent.getBorderType(facesContext);
                }

                if (borderType != null) {
                    borderTypeCapability.setBorderType(borderType);
                }
            }
        }

        htmlWriter.endComponent();
        ComponentTools.encodeRecursive(facesContext, child);

        htmlWriter.endElement(IHtmlWriter.LI);
    }

    private void encodeToolItemSeparator(IHtmlWriter htmlWriter,
            ToolBarComponent toolBarComponent,
            ISeparatorComponent separatorComponent) throws WriterException {

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        IToolBarImageAccessors toolBarImageAccessors = (IToolBarImageAccessors) toolBarComponent
                .getImageAccessors(facesContext);

        IContentAccessor separatorImageURL = toolBarImageAccessors
                .getSeparatorImageAccessor();

        htmlWriter.startElement(IHtmlWriter.LI);

        StringAppender sa = new StringAppender("f_toolFolder_itemSeparator", 32);
        if (separatorImageURL == null) {
            sa.append(" f_toolFolder_autoSeparator");
        }
        htmlWriter.writeClass(sa.toString());

        if (toolBarComponent.isItemPaddingSetted()) {
            int cellPadding = toolBarComponent.getItemPadding(facesContext);
            if (cellPadding >= 0) {
                htmlWriter.writeStyle().writePadding(cellPadding + "px");
            }
        }

        htmlWriter.writeAttributeNS("separator", true);

        if (separatorImageURL != null) {

            String imageURL = separatorImageURL.resolveURL(facesContext, null,
                    null);
            if (imageURL != null) {
                htmlWriter.startElement(IHtmlWriter.IMG);
                htmlWriter.writeClass("f_toolFolder_imgSeparator");

                int imageWidth = toolBarComponent
                        .getSeparatorImageWidth(facesContext);
                if (imageWidth > 0) {
                    htmlWriter.writeWidth(imageWidth);
                }

                int imageHeight = toolBarComponent
                        .getSeparatorImageHeight(facesContext);
                if (imageHeight > 0) {
                    htmlWriter.writeHeight(imageHeight);
                }

                htmlWriter.writeSrc(imageURL);

                String alternateText = null;
                if (separatorComponent instanceof IAlternateTextCapability) {
                    alternateText = ((IAlternateTextCapability) separatorComponent)
                            .getAlternateText();
                }

                if (alternateText == null) {
                    alternateText = toolBarComponent
                            .getSeparatorAlternateText(facesContext);
                }

                if (alternateText != null) {
                    htmlWriter.writeAlt(alternateText);
                }

                htmlWriter.endElement(IHtmlWriter.IMG);
            }
        }

        htmlWriter.endElement(IHtmlWriter.LI);
    }

    protected String getSeparatorAlternateText(SelectItem selectItem) {
        return null;
    }

    protected int getToolItemSeparatorWidth(SelectItem selectItem) {
        return ToolBarRenderer.DEFAULT_TOOL_ITEM_SEPARATOR_WIDTH;
    }

}
