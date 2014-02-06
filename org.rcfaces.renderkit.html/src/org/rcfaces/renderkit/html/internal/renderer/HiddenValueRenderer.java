/*
 * $Id: HiddenValueRenderer.java,v 1.2 2013/01/11 15:45:02 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.HiddenValueComponent;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;


/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:02 $
 */
public class HiddenValueRenderer extends AbstractJavaScriptRenderer {

    private static final Log LOG = LogFactory.getLog(HiddenValueRenderer.class);

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        HiddenValueComponent hiddenValueComponent = (HiddenValueComponent) componentRenderContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.INPUT);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeInputAttributes(htmlWriter);

        htmlWriter.writeName(componentRenderContext.getComponentClientId());

        htmlWriter.writeType(IHtmlWriter.HIDDEN_INPUT_TYPE);
        htmlWriter.writeAutoComplete(IHtmlWriter.AUTOCOMPLETE_OFF);

        Object value = getValue(hiddenValueComponent);
        if (value != null) {
            String convertedValue = convertValue(facesContext,
                    hiddenValueComponent, value);

            if (convertedValue != null) {
                htmlWriter.writeValue(convertedValue);
            }
        }

        htmlWriter.endElement(IHtmlWriter.INPUT);

        super.encodeEnd(htmlWriter);
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        HiddenValueComponent hiddenValueComponent = (HiddenValueComponent) component;

        String newValue = componentData.getComponentParameter();

        if (newValue != null
                && hiddenValueComponent
                        .isValueLocked(context.getFacesContext()) == false) {
            hiddenValueComponent.setSubmittedExternalValue(newValue);
        }
    }

    protected void writeInputAttributes(IHtmlWriter htmlWriter) {
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.HIDDEN_VALUE;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }
}
