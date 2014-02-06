/*
 * $Id: BottomArrowBorderRenderer.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.border;

import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class BottomArrowBorderRenderer extends NoneBorderRenderer {
    

    protected String getComboImageVerticalAlign(IHtmlWriter writer) {
        return "bottom";
    }

}
