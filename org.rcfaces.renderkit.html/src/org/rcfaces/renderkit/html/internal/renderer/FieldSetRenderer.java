/*
 * $Id: FieldSetRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;

import org.rcfaces.core.component.FieldSetComponent;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.renderkit.border.IBorderRenderersRegistry;
import org.rcfaces.core.internal.renderkit.border.ITitledBorderRenderer;
import org.rcfaces.core.internal.renderkit.designer.IDesignerEngine;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.border.IFieldSetBorderRenderer;
import org.rcfaces.renderkit.html.internal.border.IHtmlBorderRenderer;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
@XhtmlNSAttributes({ "overStyleClass" })
public class FieldSetRenderer extends AbstractCssRenderer {

    private static final String BORDER_RENDERER = "camelia.customButton.borderRenderer";

    private static final String CONTENT = "_content";

    private static final String TITLE_ID_SUFFIX = ""
            + UINamingContainer.SEPARATOR_CHAR
            + UINamingContainer.SEPARATOR_CHAR + "label";

    public final void encodeBegin(IComponentWriter writer)
            throws WriterException {
        // Final a cause du designer

        super.encodeBegin(writer);

        IComponentRenderContext componentContext = writer
                .getComponentRenderContext();

        FieldSetComponent component = (FieldSetComponent) componentContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeFieldSetTop(htmlWriter, component);

        designerBeginChildren(writer, IDesignerEngine.MAIN_BODY);
    }

    protected void encodeFieldSetTop(IHtmlWriter htmlWriter,
            FieldSetComponent fieldSetComponent) throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeFieldSetAttributes(htmlWriter, fieldSetComponent);

        String textAlignement = fieldSetComponent
                .getTextAlignment(facesContext);
        String verticalAlignement = fieldSetComponent
                .getVerticalAlignment(facesContext);

        IHtmlBorderRenderer borderRenderer = getHtmlBorderRenderer(htmlWriter,
                fieldSetComponent);

        if (borderRenderer != null) {
            componentRenderContext
                    .setAttribute(BORDER_RENDERER, borderRenderer);

            String width = fieldSetComponent.getWidth(facesContext);
            String height = fieldSetComponent.getHeight(facesContext);

            borderRenderer.initialize(htmlWriter,
                    getCssStyleClasses(htmlWriter), width, height, 1, 1, false,
                    false);

            if (borderRenderer instanceof ITitledBorderRenderer) {
                String text = fieldSetComponent.getText(facesContext);
                if (text != null) {
                    text = ParamUtils.formatMessage(fieldSetComponent, text);
                }

                ((ITitledBorderRenderer) borderRenderer).setText(htmlWriter,
                        text, componentRenderContext.getComponentClientId()
                                + TITLE_ID_SUFFIX);

            }

            borderRenderer.startComposite(htmlWriter);

            borderRenderer.startRow(htmlWriter);

            borderRenderer.startChild(htmlWriter,
                    IFieldSetBorderRenderer.CELL_BODY_SUFFIX, textAlignement,
                    verticalAlignement, null, null, 1, 1);
        }

        htmlWriter.startElement(IHtmlWriter.DIV);
        htmlWriter.writeClass(getBodyClassName(htmlWriter));

        if (borderRenderer == null) {
            // On aligne par CSS ...

            ICssWriter cssWriter = htmlWriter.writeStyle(64);

            if (textAlignement != null) {
                cssWriter.writeTextAlign(textAlignement);
            }

            if (verticalAlignement != null) {
                cssWriter.writeVerticalAlign(verticalAlignement);
            }
        }

        String height = fieldSetComponent.getHeight(facesContext);
        if (height != null) {
            int delta = 0;

            if (borderRenderer != null) {
                int nd = borderRenderer.getNorthBorderHeight();
                int sd = borderRenderer.getSouthBorderHeight();

                if (nd >= 0 && sd >= 0) {
                    delta = -(nd + sd);
                }
            }

            String hs = computeSizeInPixel(height, -1, delta);
            if (hs != null) {
                htmlWriter.writeStyle(32).writeHeight(hs)
                        .writeOverflow(ICssWriter.HIDDEN);
            }
        }
    }

    protected String getBodyClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName() + CONTENT;
    }

    protected void writeFieldSetAttributes(IHtmlWriter htmlWriter,
            FieldSetComponent fieldSetComponent) throws WriterException {

        writeCoreAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);

        writeStyleClass(htmlWriter, getCssStyleClasses(htmlWriter));

        ICssWriter cssWriter = htmlWriter.writeStyle(32);
        writeFieldSetCss(htmlWriter, cssWriter, fieldSetComponent);

        String overStyleClass = fieldSetComponent.getOverStyleClass(htmlWriter
                .getComponentRenderContext().getFacesContext());
        if (overStyleClass != null) {
            htmlWriter.writeAttributeNS("overStyleClass", overStyleClass);

            htmlWriter.getJavaScriptEnableMode().enableOnOver();
        }

    }

    protected void writeFieldSetCss(IHtmlWriter htmlWriter,
            ICssWriter cssWriter, FieldSetComponent fieldSetComponent)
            throws WriterException {

        writeComponentPosition(htmlWriter, cssWriter, fieldSetComponent,
                CSS_ALL_MASK);
        // cssWriter.writePosition(fieldSetComponent);
        // cssWriter.writeSize(fieldSetComponent);

        cssWriter.writeMargin(fieldSetComponent);
        cssWriter.writeVisibility(fieldSetComponent);
        cssWriter.writeBackground(fieldSetComponent, null);
    }

    protected IHtmlBorderRenderer getHtmlBorderRenderer(IHtmlWriter writer,
            FieldSetComponent fieldSetComponent) {

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        String borderType = fieldSetComponent.getBorderType(facesContext);

        IBorderRenderersRegistry borderRendererRegistry = componentRenderContext
                .getRenderContext().getProcessContext().getRcfacesContext()
                .getBorderRenderersRegistry();

        return (IHtmlBorderRenderer) borderRendererRegistry.getBorderRenderer(
                facesContext, RenderKitFactory.HTML_BASIC_RENDER_KIT,
                fieldSetComponent.getFamily(),
                fieldSetComponent.getRendererType(), borderType);
    }

    protected String getDefaultBorderType(FieldSetComponent fieldSetComponent) {
        return "rounded";
    }

    protected final void encodeEnd(IComponentWriter writer)
            throws WriterException {
        // Final a cause du designer
        designerEndChildren(writer, IDesignerEngine.MAIN_BODY);

        IComponentRenderContext componentContext = writer
                .getComponentRenderContext();

        FieldSetComponent component = (FieldSetComponent) componentContext
                .getComponent();

        encodeFieldSetBottom((IHtmlWriter) writer, component);

        super.encodeEnd(writer);
    }

    protected void encodeFieldSetBottom(IHtmlWriter htmlWriter,
            FieldSetComponent component) throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        htmlWriter.endElement(IHtmlWriter.DIV);

        IHtmlBorderRenderer borderRenderer = (IHtmlBorderRenderer) componentRenderContext
                .getAttribute(BORDER_RENDERER);

        if (borderRenderer != null) {
            borderRenderer.endChild(htmlWriter);

            borderRenderer.endRow(htmlWriter);

            borderRenderer.endComposite(htmlWriter);
        }

        htmlWriter.endElement(IHtmlWriter.DIV);
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        FieldSetComponent fieldSetComponent = (FieldSetComponent) component;

        String text = componentData.getStringProperty("text");
        if (text != null) {
            String old = fieldSetComponent.getText(facesContext);
            if (text.equals(old) == false) {
                fieldSetComponent.setText(text);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.TEXT, old, text));
            }
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.FIELD_SET;
    }

    @Override
    protected boolean needInitLayout() {
        return true;
    }

}