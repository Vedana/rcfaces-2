/*
 * $Id: LineBreakRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 *
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;


/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public class LineBreakRenderer extends AbstractCssRenderer {

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        // TextComponent textComponent = (TextComponent) writer.getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.BR);
        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        htmlWriter.endElement(IHtmlWriter.BR);

        super.encodeEnd(htmlWriter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rcfaces.core.internal.renderkit.html.AbstractHtmlRenderer#
     * getJavaScriptClassName()
     */
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.LINE_BREAK;
    }
}
