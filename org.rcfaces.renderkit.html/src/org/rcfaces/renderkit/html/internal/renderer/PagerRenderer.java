/*
 * $Id: PagerRenderer.java,v 1.3 2013/01/18 15:17:53 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.PagerComponent;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.AbstractCssRenderer;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/01/18 15:17:53 $
 */
@XhtmlNSAttributes({ "for", "noPagedMessage" })
public class PagerRenderer extends AbstractCssRenderer {

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.PAGER;
    }

    protected void encodeBegin(IComponentWriter writer) throws WriterException {
        super.encodeBegin(writer);

        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();

        PagerComponent pagerComponent = (PagerComponent) writer
                .getComponentRenderContext().getComponent();

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        htmlWriter.startElement(IHtmlWriter.DIV);
        if (pagerComponent.getWidth(facesContext) == null
                && pagerComponent.getHeight(facesContext) == null) {
            htmlWriter.writeStyle().writeDisplay(ICssWriter.INLINE);
        }

        writeHtmlAttributes(htmlWriter);
        writeJavaScriptAttributes(htmlWriter);
        writeCssAttributes(htmlWriter);

        String forValue = pagerComponent.getFor(facesContext);
        if (forValue == null) {
            throw new WriterException("'for' attribute must be specified !",
                    null, pagerComponent);
        }

        htmlWriter.writeAttributeNS("for", forValue);

        writePagerMessage(htmlWriter, pagerComponent);

        String noPagedMessage = pagerComponent.getNoPagedMessage(facesContext);
        if (noPagedMessage != null) {
            noPagedMessage = ParamUtils.formatMessage(pagerComponent,
                    noPagedMessage);

            htmlWriter.writeAttributeNS("noPagedMessage", noPagedMessage);
        }

        htmlWriter.endElement(IHtmlWriter.DIV);

        htmlWriter.getJavaScriptEnableMode().enableOnInit();
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
    		throws IOException {
    	
    	
    	
    	super.encodeEnd(context, component);
    }
}
