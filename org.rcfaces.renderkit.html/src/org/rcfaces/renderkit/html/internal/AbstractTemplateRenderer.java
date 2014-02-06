/*
 * $Id: AbstractTemplateRenderer.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public abstract class AbstractTemplateRenderer extends AbstractCssRenderer {
    

    public boolean getDecodesChildren() {
        return true;
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
    }

    // On le met sur le end, car des clientsDatas ... et autres peuvent survenir
    // ...
    public void encodeEnd(IComponentWriter writer) throws WriterException {
        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        encodeComponent(htmlWriter);

        super.encodeEnd(htmlWriter);
    }

    protected abstract void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException;
}