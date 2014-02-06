/*
 * $Id: IGeneratedImageInformation.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.image;

import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IGeneratedImageInformation extends
        IGeneratedResourceInformation {

    String WIDTH_PROPERTY = "org.rfcaces.core.model.WIDTH";

    String HEIGHT_PROPERTY = "org.rfcaces.core.model.HEIGHT";

    String ENCODER_MIME_TYPE_PROPERTY = "org.rcfaces.encoder.MIME_TYPE";

    // String ENCODER_SUFFIX_PROPERTY = "org.rcfaces.encoder.SUFFIX";

    void setImageHeight(int imageHeight);

    void setImageWidth(int imageWidth);

    String getEncoderMimeType();

    void setEncoderMimeType(String mimeType);
}
