/*
 * $Id: SubmitButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;

import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public class SubmitButtonRenderer extends ButtonRenderer {
    /*
     * protected void encodeComponent(IHtmlWriter htmlWriter) throws
     * WriterException { super.encodeComponent(htmlWriter);
     * 
     * htmlWriter.enableJavaScript(); }
     */

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        super.encodeComponent(htmlWriter);

        // Il faut l'activer pour récuperer la touche SUBMIT
        htmlWriter.getJavaScriptEnableMode().enableOnInit();
    }

    protected String getInputType(UIComponent component) {
        return IHtmlWriter.SUBMIT_INPUT_TYPE;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.SUBMIT_BUTTON;
    }
}