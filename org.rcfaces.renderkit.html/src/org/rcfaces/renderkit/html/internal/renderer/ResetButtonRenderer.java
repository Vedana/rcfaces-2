/*
 * $Id: ResetButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;

import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public class ResetButtonRenderer extends ButtonRenderer {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.html.AbstractInputRenderer#getInputType
     * ()
     */
    protected String getInputType(UIComponent component) {
        return IHtmlWriter.RESET_INPUT_TYPE;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.RESET_BUTTON;
    }
}
