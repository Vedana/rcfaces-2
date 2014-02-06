/*
 * $Id: ItemsToolFolderRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;

import org.rcfaces.core.component.ItemsToolFolderComponent;
import org.rcfaces.core.event.ItemSelectionEvent;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IEventData;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractSelectItemsRenderer;
import org.rcfaces.renderkit.html.internal.EventDecoders;
import org.rcfaces.renderkit.html.internal.EventDecoders.IEventDecoder;
import org.rcfaces.renderkit.html.internal.EventDecoders.IEventObjectDecoder;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.ItemsToolFolderDecorator;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSAttributes({ "disabled", "readOnly" })
public class ItemsToolFolderRenderer extends AbstractSelectItemsRenderer {

    private static final int IMMEDIATE_DETAIL = 0x400;

    protected static EventDecoders.IEventDecoder ITEM_SELECTION_DECODER = new EventDecoders.AbstractEventDecoder() {

        public void decodeEvent(IRequestContext requestContext,
                UIComponent component, IEventData eventData,
                IEventObjectDecoder eventObjectDecoder) {
            FacesEvent event = new ItemSelectionEvent(component,
                    eventData.getEventValue(), null, eventData.getEventItem(),
                    eventData.getEventDetail(),
                    (eventData.getEventDetail() & IMMEDIATE_DETAIL) > 0);

            queueEvent(component, event);
        }
    };

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {
    }

    protected void encodeBeforeDecorator(IHtmlWriter writer,
            IComponentDecorator componentDecorator) throws WriterException {
        super.encodeBeforeDecorator(writer, componentDecorator);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ItemsToolFolderComponent itemsToolFolderComponent = (ItemsToolFolderComponent) componentRenderContext
                .getComponent();

        writer.startElement(IHtmlWriter.UL);

        int cellPadding = itemsToolFolderComponent.getToolBar().getItemPadding(
                facesContext);
        if (cellPadding < 0) {
            cellPadding = 0;
        }
        // writer.writeCellPadding(cellPadding);

        // writer.writeCellSpacing(0);

        writeHtmlAttributes(writer);
        writeJavaScriptAttributes(writer);
        writeCssAttributes(writer);

        if (itemsToolFolderComponent.isDisabled(facesContext)) {
            writer.writeAttributeNS("disabled", true);
        }
        if (itemsToolFolderComponent.isReadOnly(facesContext)) {
            writer.writeAttributeNS("readOnly", true);
        }

        String verticalAlignment = itemsToolFolderComponent
                .getVerticalAlignment(facesContext);
        if (verticalAlignment != null) {
            writer.writeVAlign(verticalAlignment);
        }

        /*
         * String className = getMainStyleClassName(); // Un dummy pour eviter
         * des sauts de pages writer.startElement(IHtmlWriter.A"); String cls =
         * className + "_itemFolder"; writer.writeAttribute("class", cls);
         * writer.endElement(IHtmlWriter.A");
         */

        // writer.startElement(IHtmlWriter.TBODY);
        // writer.startElement(IHtmlWriter.TR);
    }

    protected void encodeAfterDecorator(IHtmlWriter writer,
            IComponentDecorator componentDecorator) throws WriterException {

        writer.endElement(IHtmlWriter.UL);

        super.encodeAfterDecorator(writer, componentDecorator);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.AbstractHtmlRenderer#
     * getJavaScriptClassName()
     */
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.ITEMS_TOOL_FOLDER;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.AbstractCameliaRenderer#
     * getDecodesChildren()
     */
    public boolean getDecodesChildren() {
        return true;
    }

    protected IEventDecoder getEventDecoder(IRequestContext context,
            UIComponent component, IEventData eventData) {
        if (eventData.getEventName().equals(JavaScriptClasses.EVENT_SELECTION)
                && eventData.getEventItem() != null) {
            return ITEM_SELECTION_DECODER;
        }

        return super.getEventDecoder(context, component, eventData);
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {
        return new ItemsToolFolderDecorator(component);
    }

}