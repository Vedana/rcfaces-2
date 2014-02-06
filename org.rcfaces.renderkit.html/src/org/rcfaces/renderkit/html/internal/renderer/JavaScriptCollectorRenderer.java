/*
 * $Id: JavaScriptCollectorRenderer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IAsyncRenderer;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.component.JavaScriptCollectorComponent;
import org.rcfaces.renderkit.html.internal.AbstractHtmlRenderer;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class JavaScriptCollectorRenderer extends AbstractHtmlRenderer {
    

    private static final Log LOG = LogFactory
            .getLog(JavaScriptCollectorRenderer.class);

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        FacesContext facesContext = htmlRenderContext.getFacesContext();

        JavaScriptCollectorComponent javaScriptCollectorComponent = (JavaScriptCollectorComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        boolean mergeScripts = javaScriptCollectorComponent
                .isMergeScripts(facesContext);

        // Pas joli ... joli ...
        AbstractJavaScriptRenderContext oldJavaScriptRenderContext = (AbstractJavaScriptRenderContext) htmlRenderContext
                .getJavaScriptRenderContext();

        IJavaScriptRenderContext newJavaScriptRenderContext = new JavaScriptCollectorRenderContext(
                oldJavaScriptRenderContext, mergeScripts);

        htmlRenderContext.pushInteractiveRenderComponent(htmlWriter,
                newJavaScriptRenderContext);
    }

    public IAsyncRenderer getAsyncRenderer(FacesContext facesContext) {
        return null;
    }
}