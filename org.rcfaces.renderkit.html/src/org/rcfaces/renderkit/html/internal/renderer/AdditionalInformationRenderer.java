/*
 * $Id: AdditionalInformationRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public class AdditionalInformationRenderer extends AbstractCssRenderer {

    private static final Log LOG = LogFactory
            .getLog(AdditionalInformationRenderer.class);

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeComponent(htmlWriter);

        htmlWriter.getHtmlComponentRenderContext().getHtmlRenderContext()
                .pushInteractiveRenderComponent(htmlWriter, null);
    }

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {

        htmlWriter.startElement(IHtmlWriter.DIV);

        writeComponentAttributes(htmlWriter);
    }

    protected void writeComponentAttributes(IHtmlWriter htmlWriter)
            throws WriterException {
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);
    }

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeEndComponent(htmlWriter);

        super.encodeEnd(writer);
    }

    protected void encodeEndComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        htmlWriter.endElement(IHtmlWriter.DIV);
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.ADDITIONAL_INFORMATION;
    }

}
