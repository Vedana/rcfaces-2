/**
 * $Id: PasswordEntryRenderer.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import javax.faces.component.UIComponent;

import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class PasswordEntryRenderer extends TextEntryRenderer {
    

    protected String getInputType(UIComponent component) {
        return IHtmlWriter.PASSWORD_INPUT_TYPE;
    }

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.PASSWORD_ENTRY;
    }
}
