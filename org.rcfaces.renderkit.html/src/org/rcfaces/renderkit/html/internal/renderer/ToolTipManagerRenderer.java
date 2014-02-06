/*
 * $Id: ToolTipManagerRenderer.java,v 1.1 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.core.component.ToolTipManagerComponent;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;


/**
 * 
 * @author jbmeslin@vadana.com (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:01 $
 */
public class ToolTipManagerRenderer extends AbstractJavaScriptRenderer {

    static final String TOOLTIP_MANAGER_DEFINED_PROPERTY = "org.rcfaces.html.TOOLTIP_MANAGER";

    protected void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext htmlComponentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();
        if (htmlComponentRenderContext.getHtmlRenderContext()
                .getJavaScriptRenderContext().isCollectorMode() == false) {

            htmlWriter.startElementNS(AbstractJavaScriptRenderer.LAZY_INIT_TAG);
            writeHtmlAttributes(htmlWriter);
            writeJavaScriptAttributes(htmlWriter);

            IHtmlComponentRenderContext componentRenderContext = htmlWriter
                    .getHtmlComponentRenderContext();

            ToolTipManagerComponent tooltipManagerComponent = (ToolTipManagerComponent) componentRenderContext
                    .getComponent();

            int delay = tooltipManagerComponent
                    .getShowDelayMs(componentRenderContext.getFacesContext());
            if (delay >= 0) {
                htmlWriter.writeAttributeNS("delay", delay);
            }

            int thresold = tooltipManagerComponent
                    .getNeighbourThresholdMs(componentRenderContext
                            .getFacesContext());
            if (thresold >= 0) {
                htmlWriter.writeAttributeNS("neighbourThreshold", thresold);
            }

            htmlWriter.endElementNS(AbstractJavaScriptRenderer.LAZY_INIT_TAG);

            if (true) {
                declareLazyJavaScriptRenderer(htmlWriter);
            }

        } else {
            htmlWriter.enableJavaScript();
        }

        htmlWriter
                .getHtmlComponentRenderContext()
                .getRenderContext()
                .setAttribute(
                        ToolTipManagerRenderer.TOOLTIP_MANAGER_DEFINED_PROPERTY,
                        Boolean.TRUE);

        super.encodeEnd(htmlWriter);
    }

    protected void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {
        super.encodeJavaScript(jsWriter);

        IHtmlComponentRenderContext componentRenderContext = jsWriter
                .getHtmlComponentRenderContext();

        ToolTipManagerComponent tooltipManagerComponent = (ToolTipManagerComponent) componentRenderContext
                .getComponent();

        if (jsWriter.getJavaScriptRenderContext().isCollectorMode() == false) {
            return;
        }

        jsWriter.setIgnoreComponentInitialization();

        String varName = jsWriter.getComponentVarName();

        jsWriter.write(varName).write('=')
                .writeCall(getJavaScriptClassName(), "Get").writeln(");");

        int delay = tooltipManagerComponent
                .getShowDelayMs(componentRenderContext.getFacesContext());
        if (delay >= 0) {
            jsWriter.writeMethodCall("f_setShowDelayMs").writeInt(delay)
                    .writeln(");");
        }

        int thresold = tooltipManagerComponent
                .getNeighbourThresholdMs(componentRenderContext
                        .getFacesContext());
        if (thresold >= 0) {
            jsWriter.writeMethodCall("f_setNeighbourThresholdMs")
                    .writeInt(thresold).writeln(");");
        }
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.TOOLTIP_MANAGER;
    }

    protected boolean sendCompleteComponent(
            IHtmlComponentRenderContext htmlComponentContext) {
        return false;
    }
}
