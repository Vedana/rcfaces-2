/*
 * $Id: NoneBorderRenderer.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.border;

import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.renderer.ICssStyleClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class NoneBorderRenderer extends AbstractHtmlBorderRenderer {
    

    public static final String NONE_BORDER_CLASS = null;

    public static final IHtmlBorderRenderer SINGLETON = new NoneBorderRenderer();

    protected boolean hasBorder() {
        return false;
    }

    protected String getClassName() {
        return NONE_BORDER_CLASS;
    }

    public void initialize(IHtmlWriter writer, ICssStyleClasses cssStyleClasses,
            String width, String height, int horizontalSpan, int verticalSpan,
            boolean disabled, boolean selected) throws WriterException {
        if (horizontalSpan < 2 && verticalSpan < 2) {
            this.noTable = true;
        }

        super.initialize(writer, cssStyleClasses, width, height,
                horizontalSpan, verticalSpan, disabled, selected);
    }
}
