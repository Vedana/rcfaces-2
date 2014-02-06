/*
 * $Id: ServiceRenderer.java,v 1.5 2013/12/12 15:39:12 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ServiceComponent;
import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;
import org.rcfaces.renderkit.html.internal.service.ServiceBehaviorListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/12/12 15:39:12 $
 */
@XhtmlNSAttributes({ "serviceId" })
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class ServiceRenderer extends AbstractJavaScriptRenderer {

    public void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IComponentRenderContext componentRenderContext = writer
                .getComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        ServiceComponent serviceComponent = (ServiceComponent) componentRenderContext
                .getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElementNS(LAZY_INIT_TAG);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        appendBehavior(serviceComponent, facesContext);

        String serviceId = serviceComponent.getServiceId(facesContext);
        if (serviceId != null) {
            htmlWriter.writeAttributeNS("serviceId", serviceId);
        }

        htmlWriter.endElementNS(LAZY_INIT_TAG);

        declareLazyJavaScriptRenderer(htmlWriter);
    }
    
    private void appendBehavior(ServiceComponent serviceComponent, FacesContext facesContext){
    	ServiceBehaviorListener.addAjaxBehavior(serviceComponent, facesContext);
    }
   

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.SERVICE;
    }

    protected void decode(IRequestContext context, UIComponent component,
            IComponentData componentData) {
        super.decode(context, component, componentData);

        // FacesContext facesContext = context.getFacesContext();

        ServiceComponent serviceComponent = (ServiceComponent) component;

        String filterExpression = componentData
                .getStringProperty("filterExpression");
        if (filterExpression != null) {
            if (filterExpression.length() < 1) {
                filterExpression = null;
            }

            serviceComponent.setFilterProperties(HtmlTools
                    .decodeFilterExpression(context.getProcessContext(),
                            component, filterExpression));
        }

    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }

}
