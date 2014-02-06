/*
 * $Id: TextEditorRenderer.java,v 1.4 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.Serializable;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TextEditorComponent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractInputRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptComponentRenderer;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.w3c.dom.Document;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "mimeType", "text" })
public class TextEditorRenderer extends AbstractInputRenderer {

    private static final IJavaScriptComponentRenderer TEXTEDITOR_JAVASCRIPT_COMPONENT = new IJavaScriptComponentRenderer() {

        public void initializeJavaScript(IJavaScriptWriter javaScriptWriter)
                throws WriterException {
            javaScriptWriter.getJavaScriptRenderContext()
                    .initializeJavaScriptDocument(javaScriptWriter);
        }

        public void initializeJavaScriptComponent(
                IJavaScriptWriter javaScriptWriter) {
        }

        public void releaseJavaScript(IJavaScriptWriter javaScriptWriter) {
        }

        public void initializePendingComponents(IJavaScriptWriter writer) {
        }

        public void addRequiredJavaScriptClassNames(IHtmlWriter writer,
                IJavaScriptRenderContext javaScriptRenderContext) {
        }
    };

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        // Il faut que la JAVASCRIPT f_textEditor soit dispo ICI ! (a cause du
        // onload)

        htmlRenderContext.getJavaScriptRenderContext().removeJavaScriptWriter(
                htmlWriter);

        IJavaScriptRenderContext javascriptRenderContext = htmlRenderContext
                .getJavaScriptRenderContext();

        // Il faut calculer les dependances
        javascriptRenderContext.computeRequires(htmlWriter, this);

        IJavaScriptWriter js = htmlRenderContext.getJavaScriptRenderContext()
                .getJavaScriptWriter(htmlWriter,
                        TEXTEDITOR_JAVASCRIPT_COMPONENT);

        initializePendingComponents(js);

        js.end();

        TextEditorComponent textEditorComponent = (TextEditorComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        htmlWriter.startElement(IHtmlWriter.IFRAME);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        htmlWriter.writeAttribute("frameborder", 0);
        htmlWriter.writeAttribute("marginwidth", 0);
        htmlWriter.writeAttribute("marginheight", 0);
        htmlWriter.writeAttribute("hspace", 0);
        htmlWriter.writeAttribute("vspace", 0);
        htmlWriter.writeName(htmlWriter.getComponentRenderContext()
                .getComponentClientId());

        if (htmlRenderContext.getJavaScriptRenderContext().isCollectorMode() == false) {
            String onLoad = "f_textEditor."
                    + htmlWriter.getHtmlComponentRenderContext()
                            .getHtmlRenderContext()
                            .getJavaScriptRenderContext()
                            .convertSymbol("f_textEditor", "_OnLoad")
                    + "(this)";
            htmlWriter.writeAttribute("onload", onLoad);
        } else {
            String onLoad = "this."
                    + htmlWriter.getHtmlComponentRenderContext()
                            .getHtmlRenderContext()
                            .getJavaScriptRenderContext()
                            .convertSymbol("f_textEditor", "_loaded") + "=true";
            htmlWriter.writeAttribute("onload", onLoad);
            htmlWriter.getJavaScriptEnableMode().enableOnInit();
        }

        Object value = textEditorComponent.getValue();

        String valueMimeType = textEditorComponent
                .getValueMimeType(facesContext);

        if (valueMimeType == null) {
            if (value instanceof String) {
                valueMimeType = "text/plain";

            } else if (value instanceof Document) {
                valueMimeType = "text/html";
            }
        }

        if (valueMimeType != null) {
            valueMimeType = valueMimeType.toLowerCase();
        }

        if ("text/plain".equals(valueMimeType)) {
            formatTextPlain(htmlWriter, value);

        } else if ("text/html".equals(valueMimeType)) {
            formatTextHtml(htmlWriter, value);

        } else {

        }

        if (valueMimeType != null) {
            htmlWriter.writeAttributeNS("mimeType", valueMimeType);
        }

        htmlWriter.endElement(IHtmlWriter.IFRAME);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();
    }

    protected void formatTextHtml(IHtmlWriter htmlWriter, Object value)
            throws WriterException {
        if (value != null) {
            htmlWriter.writeAttributeNS("text", String.valueOf(value));
        }
    }

    protected void formatTextPlain(IHtmlWriter htmlWriter, Object value)
            throws WriterException {
        if (value != null) {
            htmlWriter.writeAttributeNS("text", String.valueOf(value));
        }
    }

    protected boolean useHtmlAccessKeyAttribute() {
        return true;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TEXT_EDITOR;
    }

    protected String getInputType(UIComponent component) {
        return null; //
    }

    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        TextEditorComponent textAreaComponent = (TextEditorComponent) component;

        String newValue = componentData.getStringProperty(Properties.VALUE);

        if (newValue != null
                && textAreaComponent.isValueLocked(context.getFacesContext()) == false) {
            textAreaComponent.setSubmittedExternalValue(newValue);
        }
    }

    protected void addUnlockProperties(Set<Serializable> unlockedProperties) {
        super.addUnlockProperties(unlockedProperties);

        unlockedProperties.add(Properties.VALUE);
    }

}