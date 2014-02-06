/*
 * $Id: ImageResetButtonRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import org.rcfaces.renderkit.html.internal.JavaScriptClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public class ImageResetButtonRenderer extends ImageButtonRenderer {

    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_RESET_BUTTON;
    }
}