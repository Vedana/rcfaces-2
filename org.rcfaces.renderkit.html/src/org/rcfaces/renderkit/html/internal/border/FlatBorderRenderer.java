/*
 * $Id: FlatBorderRenderer.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.border;

import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.ns.XhtmlNSAttributes;
import org.rcfaces.renderkit.html.internal.renderer.ICssStyleClasses;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
@XhtmlNSAttributes({ "flatMode" })
public class FlatBorderRenderer extends AbstractHtmlBorderRenderer {

    private static final String FLAT_BORDER_CLASS = "fb_flat";

    public void initialize(IHtmlWriter writer,
            ICssStyleClasses cssStyleClasses, String width, String height,
            int horizontalSpan, int verticalSpan, boolean disabled,
            boolean selected) throws WriterException {
        super.initialize(writer, cssStyleClasses, width, height,
                horizontalSpan, verticalSpan, disabled, selected);

        writer.writeAttributeNS("flatMode", true);
    }

    protected boolean hasBorder() {
        return true;
    }

    protected String getClassName() {
        return FLAT_BORDER_CLASS;
    }
}
