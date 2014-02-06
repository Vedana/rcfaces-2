/*
 * $Id: ViewErrorListenerRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSElement;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
@XhtmlNSElement(AbstractJavaScriptRenderer.LAZY_INIT_TAG)
public class ViewErrorListenerRenderer extends AbstractJavaScriptRenderer {

    public void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext htmlComponentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();
        if (htmlComponentRenderContext.getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {

            htmlWriter.startElementNS(LAZY_INIT_TAG);
            writeHtmlAttributes(htmlWriter);
            writeJavaScriptAttributes(htmlWriter);

            htmlWriter.endElementNS(LAZY_INIT_TAG);

            declareLazyJavaScriptRenderer(htmlWriter);

        } else {
            htmlWriter.enableJavaScript();
        }
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        if (jsWriter.getJavaScriptRenderContext().isCollectorMode() == false) {
            return;
        }

        jsWriter.setIgnoreComponentInitialization();

        String varName = jsWriter.getJavaScriptRenderContext()
                .allocateVarName();
        jsWriter.setComponentVarName(varName);

        jsWriter.write(varName).write('=')
                .writeCall(getJavaScriptClassName(), "f_newInstance")
                .writeln(");");
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.VIEW_ERROR_LISTENER;
    }

    protected boolean encodeEventsInAttributes(IHtmlWriter writer) {
        return writer.getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }
}