/*
 * $Id: FocusManagerRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.FocusManagerComponent;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "focusId", "setFocusIfMessage" })
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class FocusManagerRenderer extends AbstractJavaScriptRenderer {

    protected static final String NONE_FOCUS_ID = "--none--";

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext htmlComponentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = htmlComponentRenderContext
                .getFacesContext();

        if (htmlComponentRenderContext.getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {

            htmlWriter.startElementNS(LAZY_INIT_TAG);

            writeHtmlAttributes(htmlWriter);
            writeJavaScriptAttributes(htmlWriter);

            // boolean lazy = true;

            String focusId = getFocusId(htmlComponentRenderContext);
            if (focusId != null) {
                htmlWriter.writeAttributeNS("focusId", focusId);

                // lazy = false;
            }

            FocusManagerComponent focusManagerComponent = (FocusManagerComponent) htmlComponentRenderContext
                    .getComponent();

            if (focusManagerComponent.isSetFocusIfMessageSetted()
                    && focusManagerComponent.isSetFocusIfMessage(facesContext) == false) {
                htmlWriter.writeAttributeNS("setFocusIfMessage", false);

                // lazy = false;
            }

            if (focusManagerComponent.isAutoFocus(facesContext)) {
                htmlWriter.writeAttributeNS("autoFocus", true);

                String from = focusManagerComponent
                        .getAutoFocusFrom(facesContext);
                if (from != null) {
                    htmlWriter.writeAttributeNS("autoFocusFrom", from);
                }
            }

            htmlWriter.endElementNS(LAZY_INIT_TAG);

            if (true) {
                declareLazyJavaScriptRenderer(htmlWriter);
            }

        } else {
            htmlWriter.enableJavaScript();
        }

        super.encodeEnd(htmlWriter);
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        if (jsWriter.getJavaScriptRenderContext().isCollectorMode() == false) {
            return;
        }

        FacesContext facesContext = jsWriter.getFacesContext();

        FocusManagerComponent focusManagerComponent = (FocusManagerComponent) jsWriter
                .getComponentRenderContext().getComponent();

        jsWriter.setIgnoreComponentInitialization();

        jsWriter.writeCall(getJavaScriptClassName(), "Get")
                .write(").")
                .write(jsWriter.getJavaScriptRenderContext().convertSymbol(
                        getJavaScriptClassName(), "f_initialize"))
                .write('(')
                .writeString(
                        jsWriter.getComponentRenderContext()
                                .getComponentClientId());

        int pred = 0;
        String focusId = getFocusId(jsWriter.getHtmlComponentRenderContext());
        if (focusId != null && focusId.length() > 0) {
            jsWriter.write(',').writeString(focusId);
        } else {
            pred++;
        }

        if (focusManagerComponent.isSetFocusIfMessageSetted()
                && focusManagerComponent.isSetFocusIfMessage(facesContext) == false) {
            for (; pred > 0; pred--) {
                jsWriter.write(',').writeNull();
            }

            jsWriter.write(',').writeBoolean(false);
        } else {
            pred++;
        }

        if (focusManagerComponent.isAutoFocus(facesContext)) {
            for (; pred > 0; pred--) {
                jsWriter.write(',').writeNull();
            }

            jsWriter.write(',').writeBoolean(true);

            String from = focusManagerComponent.getAutoFocusFrom(facesContext);
            if (from != null) {
                jsWriter.write(',').writeString(from);

            } else {
                pred++;
            }
        } else {
            pred += 2;
        }

        jsWriter.writeln(");");
    }

    protected String getFocusId(
            IHtmlComponentRenderContext componentRenderContext) {
        FocusManagerComponent focusManagerComponent = (FocusManagerComponent) componentRenderContext
                .getComponent();

        return focusManagerComponent.getFocusId(componentRenderContext
                .getFacesContext());
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        FocusManagerComponent focusManagerComponent = (FocusManagerComponent) component;

        String focusId = componentData.getStringProperty("focusId");
        if (focusId != null) {
            if (focusId.length() < 1) {
                focusId = null;
            }

            String oldFocusId = focusManagerComponent.getFocusId(facesContext);

            if (oldFocusId != focusId
                    && (oldFocusId == null || oldFocusId.equals(focusId) == false)) {
                focusManagerComponent.setFocusId(focusId);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.FOCUS_ID, oldFocusId, focusId));
            }
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.FOCUS_MANAGER;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }
}
