/*
 * $Id: ExternalBoxRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ExternalBoxComponent;
import org.rcfaces.core.event.PropertyChangeEvent;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IAsyncRenderer;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * 
 * @author jbmeslin@vedana.com
 * @version $Revision: 1.2 $Date: 2010/11/08
 */
@XhtmlNSAttributes({ "overStyleClass", "contentURL" })
public class ExternalBoxRenderer extends AbstractCssRenderer implements
        IAsyncRenderer {

    public void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IComponentRenderContext componentContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentContext.getFacesContext();

        ExternalBoxComponent component = (ExternalBoxComponent) componentContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.IFRAME);

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        String overStyleClass = component.getOverStyleClass(facesContext);
        if (overStyleClass != null) {
            htmlWriter.writeAttributeNS("overStyleClass", overStyleClass);

        }

        String scrolling = component.getScrolling(facesContext);
        if (scrolling != null) {
            htmlWriter.writeAttribute("scrolling", scrolling);
        }

        htmlWriter.writeAttribute("name", component.getClientId(facesContext));

        Object value = component.getValue();

        String src = String.valueOf(value);
        if (src != null) {
            IContentAccessor contentAccessor = ContentAccessorFactory
                    .createFromWebResource(facesContext, src,
                            IContentFamily.JSP);

            src = contentAccessor.resolveURL(facesContext, null, null);
            htmlWriter.writeURIAttributeNS("contentURL", src);
            if (component.isVisible()) {
                htmlWriter.writeSrc(src);
            }
        }
        htmlWriter.getJavaScriptEnableMode().enableOnInit();
        htmlWriter.endElement(IHtmlWriter.IFRAME);
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        // FacesContext facesContext = context.getFacesContext();

        ExternalBoxComponent externalBoxComponent = (ExternalBoxComponent) component;

        String newValue = componentData.getStringProperty("contentURL");

        if (newValue != null) {

            // On considere la VALUE
            String old = externalBoxComponent.getContentURL();

            Object value = newValue;

            if (old != value && (old == null || old.equals(value) == false)) {
                externalBoxComponent.setValue(value);

                component.queueEvent(new PropertyChangeEvent(component,
                        Properties.VALUE, old, value));
            }
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.EXTERNAL_BOX;
    }
}