/*
 * $Id: ImageButtonRenderer.java,v 1.2 2013/11/13 15:52:40 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.renderer;


/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:40 $
 */
public class ImageButtonRenderer extends ImageRenderer {
 
    @Override
    protected boolean isItemSelectable() {
        return true;
    }

    @Override
    protected String getJavaScriptClassName() {
        return JavaScriptClasses.IMAGE_BUTTON;
    }

}
