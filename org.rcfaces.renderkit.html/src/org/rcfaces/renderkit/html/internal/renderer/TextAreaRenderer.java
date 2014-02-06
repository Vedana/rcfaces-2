/**
 * $Id: TextAreaRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TextAreaComponent;
import org.rcfaces.core.component.TextEntryComponent;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractInputRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlProcessContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.util.CarriageReturnNormalizerMode;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
@XhtmlNSAttributes({ "maxTextLength", "ignoreWhenFull" })
public class TextAreaRenderer extends AbstractInputRenderer {

    @SuppressWarnings("unused")
    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {

        IComponentRenderContext componentRenderContext = htmlWriter
                .getComponentRenderContext();

        TextAreaComponent textAreaComponent = (TextAreaComponent) componentRenderContext
                .getComponent();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        htmlWriter.startElement(IHtmlWriter.TEXTAREA);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);
        writeInputAttributes(htmlWriter);
        writeTextDirection(htmlWriter, textAreaComponent);
        writeTextAreaAttributes(htmlWriter);

        String text = textAreaComponent.getText(facesContext);
        if (text != null) {
            int maxTextLength = textAreaComponent
                    .getMaxTextLength(facesContext);
            if (maxTextLength > 0 && text.length() > maxTextLength) {
                text = text.substring(0, maxTextLength);
            }

            htmlWriter.writeText(text);
        }

        htmlWriter.endElement(IHtmlWriter.TEXTAREA);

        htmlWriter.addSubFocusableComponent(htmlWriter
                .getComponentRenderContext().getComponentClientId());

        if (textAreaComponent.isRequired()) {
            // Il nous faut le javascript, car c'est un traitement javascript !
            htmlWriter.getJavaScriptEnableMode().enableOnSubmit();
        }

        String value = null; // (String) componentRenderContext.getAttribute(
        // VALIDATOR_INTERNAL_VALUE_ATTRIBUTE);

        if (value != null
                && value.equals(text) == false
                && htmlWriter.getJavaScriptEnableMode().isOnInitEnabled() == false) {

            htmlWriter.startElement(IHtmlWriter.INPUT);
            htmlWriter.writeType(IHtmlWriter.HIDDEN_INPUT_TYPE);
            htmlWriter.writeAutoComplete(IHtmlWriter.AUTOCOMPLETE_OFF);

            String name = componentRenderContext.getComponentClientId()
                    + "::value";
            htmlWriter.writeName(name);

            htmlWriter.writeValue(value);

            htmlWriter.endElement(IHtmlWriter.INPUT);
        }
    }

    protected void writeTextAreaAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        TextAreaComponent textAreaComponent = (TextAreaComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        int col = textAreaComponent.getColumnNumber(facesContext);
        if (col > 0) {
            htmlWriter.writeCols(col);
        }
        int row = textAreaComponent.getRowNumber(facesContext);
        if (row > 0) {
            htmlWriter.writeRows(row);
        }

        int maxTextLength = textAreaComponent.getMaxTextLength(facesContext);
        if (maxTextLength > 0) {
            htmlWriter.writeAttributeNS("maxTextLength", maxTextLength);

            // htmlWriter.enableJavaScript(); Bof bof ... on peut le faire à la
            // génération ?

            htmlWriter.getJavaScriptEnableMode().enableOnFocus();
        }

        if (textAreaComponent.isIgnoreWhenFull(facesContext)) {
            htmlWriter.writeAttributeNS("ignoreWhenFull", true);
        }

        if (textAreaComponent.isRequired()) {
            htmlWriter.writeAriaRequired(true);
        }
    }

    protected boolean useHtmlAccessKeyAttribute() {
        return true;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TEXT_AREA;
    }

    protected String getInputType(UIComponent component) {
        return null; //
    }

    protected boolean hasComponenDecoratorSupport() {
        return true;
    }

    protected void writeValueAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        TextEntryComponent textEntryComponent = (TextEntryComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        String text = convertValue(facesContext, textEntryComponent);
        if (text != null) {
            htmlWriter.writeValue(text);
        }
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        FacesContext facesContext = context.getFacesContext();

        TextAreaComponent textAreaComponent = (TextAreaComponent) component;

        String newValue = componentData.getStringProperty("text");

        if (newValue == null) {

            // Le TextArea est disabled ou en lazy-init inchangé
            String name = textAreaComponent.getClientId(facesContext)
                    + "::value";
            newValue = componentData.getParameter(name);

            if (newValue == null) {
                // Toujours rien ... on essaye les données du form !
                newValue = componentData.getComponentParameter();
            }
        }

        if (newValue != null && newValue.length() > 0) {
            IHtmlProcessContext htmlProcessContext = (IHtmlProcessContext) context
                    .getProcessContext();

            CarriageReturnNormalizerMode normalizer = htmlProcessContext
                    .getCarriageReturnNormalizerMode();

            if (normalizer != null) {
                switch (normalizer) {
                case NormalizeToCR:
                    newValue = newValue.replace("\r\n", "\r");
                    newValue = newValue.replace("\n", "\r");
                    break;

                case NormalizeToLF:
                    newValue = newValue.replace("\r\n", "\n");
                    newValue = newValue.replace("\r", "\n");
                    break;

                case NormalizeToCRLF:
                    newValue = newValue.replace("\r\n", "\n");
                    newValue = newValue.replace("\r", "\n");
                    newValue = newValue.replace("\n", "\r\n");
                    break;
                }
            }
        }

        if (newValue != null
                && textAreaComponent.isValueLocked(facesContext) == false) {
            textAreaComponent.setSubmittedExternalValue(newValue);
        }
    }

}