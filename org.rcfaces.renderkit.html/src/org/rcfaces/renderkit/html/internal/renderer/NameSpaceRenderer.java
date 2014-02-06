/*
 * $Id: NameSpaceRenderer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.component.NameSpaceComponent;
import org.rcfaces.renderkit.html.internal.AbstractHtmlRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class NameSpaceRenderer extends AbstractHtmlRenderer {

    

    private static final Log LOG = LogFactory.getLog(NameSpaceRenderer.class);

    private static final String CAMELIA_DEFAULT_PREFIX = "v";

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        NameSpaceComponent nameSpaceComponent = (NameSpaceComponent) componentRenderContext
                .getComponent();

        String prefix = nameSpaceComponent.getPrefix(facesContext);
        if (prefix == null) {
            prefix = CAMELIA_DEFAULT_PREFIX;
        }

        htmlWriter.startElement(prefix + ":ns");

        String uri = nameSpaceComponent.getUri(facesContext);
        if (uri == null && CAMELIA_DEFAULT_PREFIX.equals(prefix)) {
            uri = htmlWriter.getHtmlComponentRenderContext()
                    .getHtmlRenderContext().getHtmlProcessContext()
                    .getNameSpaceURI();
        }

        if (uri != null) {
            htmlWriter.writeAttribute(prefix + ":xmlns", uri);
        }
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext componentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = componentRenderContext.getFacesContext();

        NameSpaceComponent nameSpaceComponent = (NameSpaceComponent) componentRenderContext
                .getComponent();

        String prefix = nameSpaceComponent.getPrefix(facesContext);
        if (prefix == null) {
            prefix = CAMELIA_DEFAULT_PREFIX;
        }

        htmlWriter.endElement(prefix + ":ns");

        super.encodeEnd(writer);
    }

}
