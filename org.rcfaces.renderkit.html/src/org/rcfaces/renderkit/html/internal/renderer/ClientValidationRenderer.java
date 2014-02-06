/*
 * $Id: ClientValidationRenderer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class ClientValidationRenderer extends AbstractJavaScriptRenderer {
    

    private static final Log LOG = LogFactory
            .getLog(ClientValidationRenderer.class);

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        /*
         * IComponentRenderContext componentRenderContext = writer
         * .getComponentRenderContext(); // FacesContext facesContext =
         * componentRenderContext.getFacesContext();
         * 
         * ClientValidationComponent clientValidationComponent =
         * (ClientValidationComponent) componentRenderContext .getComponent();
         * 
         * FacesListener fls[] = clientValidationComponent
         * .listValidationListeners();
         * 
         * StringAppender sa = new StringAppender(128);
         * 
         * EventsRenderer.encodeAttributeEventListeners(writer
         * .getComponentRenderContext().getRenderContext(), sa,
         * "clientValidation", fls, false);
         * 
         * if (sa.length() < 1) { return; }
         * 
         * IHtmlWriter htmlWriter = (IHtmlWriter) writer;
         * 
         * htmlWriter.startElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG);
         * writeHtmlAttributes(htmlWriter);
         * writeJavaScriptAttributes(htmlWriter);
         * 
         * htmlWriter.endElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG);
         * 
         * setAlreadyLazyComponent(htmlWriter);
         * 
         * super.encodeEnd(htmlWriter);
         */
    }

    protected String getJavaScriptClassName() {
        return null;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }
}
