/*
 * $Id: SVGGeneratedInformation.java,v 1.1 2013/11/13 15:52:39 jbmeslin Exp $
 */
package org.rcfaces.renderkit.svg.internal.image;

import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 15:52:39 $
 */
public class SVGGeneratedInformation extends BasicGeneratedResourceInformation
        implements ISVGGeneratedInformation {

    public final int getHeight() {
        Integer i = (Integer) getAttribute(HEIGHT_PROPERTY);
        if (i == null) {
            return 0;
        }

        return i.intValue();
    }

    public final void setHeight(int imageHeight) {
        setAttribute(HEIGHT_PROPERTY, new Integer(imageHeight));
    }

    public final int getWidth() {
        Integer i = (Integer) getAttribute(WIDTH_PROPERTY);
        if (i == null) {
            return 0;
        }

        return i.intValue();
    }

    public final void setWidth(int imageWidth) {
        setAttribute(WIDTH_PROPERTY, new Integer(imageWidth));
    }

}
