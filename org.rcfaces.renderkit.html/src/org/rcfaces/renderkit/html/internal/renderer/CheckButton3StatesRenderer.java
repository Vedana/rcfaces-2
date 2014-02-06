/*
 * $Id: CheckButton3StatesRenderer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;

import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.AbstractInputRenderer;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class CheckButton3StatesRenderer extends AbstractInputRenderer {
    

    protected void encodeComponent(IHtmlWriter htmlWriter)
            throws WriterException {
        htmlWriter.writeText("Composant: checkButton3States");
    }

    protected String getWAIRole() {
        return IAccessibilityRoles.CHECK_BOX_3_STATE;
    }

    protected String getInputType(UIComponent component) {
        return "image";
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.CHECK_BUTTON_3_STATES;
    }
}
