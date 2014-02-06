/*
 * $Id: CustomButtonRenderer.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.CustomButtonComponent;
import org.rcfaces.core.component.capability.ISelectedCapability;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.renderkit.border.IBorderRenderersRegistry;
import org.rcfaces.core.internal.tools.ComponentTools;
import org.rcfaces.core.internal.tools.ValuesTools;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.border.AbstractHtmlBorderRenderer;
import org.rcfaces.renderkit.html.internal.border.IHtmlBorderRenderer;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.util.ListenerTools.INameSpace;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
@XhtmlNSAttributes({ "borderType", "disabled", "readOnly", "value" })
public class CustomButtonRenderer extends AbstractCssRenderer {
    

    private static final Log LOG = LogFactory
            .getLog(CustomButtonRenderer.class);

    private static final String BORDER_RENDERER = "camelia.customButton.borderRenderer";

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.CUSTOM_BUTTON;
    }

    public void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        CustomButtonComponent component = (CustomButtonComponent) componentRenderContext
                .getComponent();

        IHtmlBorderRenderer borderRenderer = null;
        String borderType = null;
        if (component.isBorder(facesContext)) {
            borderType = component.getBorderType(facesContext);

            IBorderRenderersRegistry borderRendererRegistry = componentRenderContext
                    .getRenderContext().getProcessContext().getRcfacesContext()
                    .getBorderRenderersRegistry();

            borderRenderer = (IHtmlBorderRenderer) borderRendererRegistry
                    .getBorderRenderer(facesContext,
                            RenderKitFactory.HTML_BASIC_RENDER_KIT,
                            component.getFamily(), component.getRendererType(),
                            borderType);
        }

        boolean disabled = component.isDisabled(facesContext);

        String width = component.getWidth(facesContext);
        String height = component.getHeight(facesContext);

        boolean selected = false;
        if (component instanceof ISelectedCapability) {
            ISelectedCapability selectedCapability = (ISelectedCapability) component;

            selected = selectedCapability.isSelected();
        }

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.DIV);

        if (borderRenderer != null) {
            htmlWriter.writeAttributeNS("borderType", borderType);
        }

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        encodeAttributes(htmlWriter, component);

        if (borderRenderer != null) {
            borderRenderer.initialize(htmlWriter,
                    getCssStyleClasses(htmlWriter), width, height, 1, 1,
                    disabled, selected);

            borderRenderer.startComposite(htmlWriter);

            componentRenderContext
                    .setAttribute(BORDER_RENDERER, borderRenderer);
        }
        /*
         * Le javascript s'occupe de ca ! if (button == false && imageJavascript
         * == false) { htmlWriter.writeAttribute("href",
         * IHtmlWriter.JAVASCRIPT_VOID); }
         */

        htmlWriter.enableJavaScript();
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {

        IRenderContext renderContext = getRenderContext(facesContext);

        IHtmlWriter htmlWriter = (IHtmlWriter) renderContext
                .getComponentWriter();

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        IHtmlBorderRenderer borderRenderer = (IHtmlBorderRenderer) componentRenderContext
                .getAttribute(BORDER_RENDERER);

        if (borderRenderer == null) {
            htmlWriter.startElement(IHtmlWriter.A);

            htmlWriter.writeClass(getLinkClassName(htmlWriter));

            encodeChildren(htmlWriter);

            htmlWriter.endElement(IHtmlWriter.A);

            return;
        }

        borderRenderer.startRow(htmlWriter);

        try {
            borderRenderer.startChild(htmlWriter,
                    AbstractHtmlBorderRenderer.TD_TEXT);
            try {
                htmlWriter.startElement(IHtmlWriter.A);

                htmlWriter.writeClass(getLinkClassName(htmlWriter));

                encodeChildren(htmlWriter);

                htmlWriter.endElement(IHtmlWriter.A);

            } finally {
                borderRenderer.endChild(htmlWriter);
            }

        } finally {
            borderRenderer.endRow(htmlWriter);
        }

    }

    protected String getLinkClassName(IHtmlWriter htmlWriter) {
        return getMainStyleClassName() + "_link";
    }

    protected void encodeChildren(IComponentWriter writer)
            throws WriterException {

        ((IHtmlWriter) writer).endComponent();

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        ComponentTools.encodeChildrenRecursive(
                componentRenderContext.getFacesContext(),
                componentRenderContext.getComponent());
    }

    public void encodeEnd(IComponentWriter writer) throws WriterException {

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        IHtmlBorderRenderer borderRenderer = (IHtmlBorderRenderer) componentRenderContext
                .getAttribute(BORDER_RENDERER);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        if (borderRenderer != null) {
            borderRenderer.endComposite(htmlWriter);
        }

        htmlWriter.endElement(IHtmlWriter.DIV);

        super.encodeEnd(htmlWriter);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.rcfaces.core.internal.renderkit.html.AbstractHtmlRenderer#
     * encodeJavaScript
     * (org.rcfaces.core.internal.renderkit.html.IJavaScriptWriter)
     * 
     * protected void encodeJavaScript(IJavaScriptWriter htmlWriter) throws
     * WriterException { super.encodeJavaScript(htmlWriter);
     * 
     * IComponentRenderContext componentContext = htmlWriter
     * .getComponentRenderContext();
     * 
     * FacesContext facesContext = componentContext.getFacesContext(); }
     */

    protected String getActionEventName(INameSpace nameSpace) {
        return nameSpace.getSelectionEventName();
    }

    protected void encodeAttributes(IHtmlWriter htmlWriter,
            CustomButtonComponent component) throws WriterException {
        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        if (component.isDisabled(facesContext)) {
            htmlWriter.writeAttributeNS("disabled", true);
        }

        if (component.isReadOnly(facesContext)) {
            htmlWriter.writeAttributeNS("readOnly", true);
        }

        Object value = component.getValue();
        if (value != null) {
            String svalue = ValuesTools.convertValueToString(value, component,
                    facesContext);

            if (svalue != null) {
                htmlWriter.writeAttributeNS("value", svalue);
            }
        }
    }
}