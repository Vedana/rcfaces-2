/*
 * $Id: NoComboMarkBorderRenderer.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.border;

import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class NoComboMarkBorderRenderer extends NoneBorderRenderer {
    

    protected boolean hasBorder() {
        return false;
    }

    protected String getClassName() {
        return NONE_BORDER_CLASS;
    }

    public void writeComboImage(IHtmlWriter writer, String componentClassName) {
    }
}
