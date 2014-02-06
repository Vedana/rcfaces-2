/*
 * $Id: GeneratedImageInformation.java,v 1.3 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.image;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:26 $
 */
public class GeneratedImageInformation extends
        BasicGeneratedResourceInformation implements IGeneratedImageInformation {

    private static final Log LOG = LogFactory
            .getLog(GeneratedImageInformation.class);

    public GeneratedImageInformation() {
        super();
    }

    public final int getImageHeight() {
        Integer i = (Integer) getAttribute(HEIGHT_PROPERTY);
        if (i == null) {
            return 0;
        }

        return i.intValue();
    }

    public final void setImageHeight(int imageHeight) {
        setAttribute(HEIGHT_PROPERTY, new Integer(imageHeight));
    }

    public final int getImageWidth() {
        Integer i = (Integer) getAttribute(WIDTH_PROPERTY);
        if (i == null) {
            return 0;
        }

        return i.intValue();
    }

    public final void setImageWidth(int imageWidth) {
        setAttribute(WIDTH_PROPERTY, new Integer(imageWidth));
    }

    public final String getEncoderMimeType() {
        return (String) getAttribute(ENCODER_MIME_TYPE_PROPERTY);
    }

    public final void setEncoderMimeType(String contentType) {
        setAttribute(ENCODER_MIME_TYPE_PROPERTY, contentType);
    }

}
