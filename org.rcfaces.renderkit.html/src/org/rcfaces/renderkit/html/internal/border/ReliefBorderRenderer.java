/*
 * $Id: ReliefBorderRenderer.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.border;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class ReliefBorderRenderer extends AbstractHtmlBorderRenderer {
    

    private static final String RELIEF_BORDER_CLASS = "fb_relief";

    protected boolean hasBorder() {
        return true;
    }

    protected String getClassName() {
        return RELIEF_BORDER_CLASS;
    }

}
