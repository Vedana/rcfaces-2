/*
 * $Id: GenerationImageInformation.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.image;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public class GenerationImageInformation extends
        BasicGenerationResourceInformation implements
        IGenerationImageInformation {

    private static final Log LOG = LogFactory
            .getLog(GenerationImageInformation.class);

    public GenerationImageInformation() {
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

    public String getEncoderMimeType() {
        return (String) getAttribute(ENCODER_MIME_TYPE_PROPERTY);
    }

    public String getEncoderSuffix() {
        return (String) getAttribute(ENCODER_SUFFIX_PROPERTY);
    }

    public void setEncoderMimeType(String mimeType) {
        setAttribute(ENCODER_MIME_TYPE_PROPERTY, mimeType);
    }

    public void setEncoderSuffix(String suffix) {
        setAttribute(ENCODER_SUFFIX_PROPERTY, suffix);
    }

}
