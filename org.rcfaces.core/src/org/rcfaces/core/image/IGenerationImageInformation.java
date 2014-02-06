/*
 * $Id: IGenerationImageInformation.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.image;

import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IGenerationImageInformation extends
        IGenerationResourceInformation {

    String WIDTH_PROPERTY = "org.rfcaces.core.model.WIDTH";

    String HEIGHT_PROPERTY = "org.rfcaces.core.model.HEIGHT";

    String ENCODER_MIME_TYPE_PROPERTY = "org.rcfaces.encoder.MIME_TYPE";

    String ENCODER_SUFFIX_PROPERTY = "org.rcfaces.encoder.SUFFIX";

    String COMPRESSION_QUALITY = "org.rfcaces.encore.COMPRESSION_QUALITY";

    String COMPRESSION_MODE = "org.rfcaces.encore.COMPRESSION_MODE";

    String COMPRESSION_TYPE = "org.rfcaces.encore.COMPRESSION_TYPE";

    String COMPRESSION_PROGRESSIVE_MODE = "org.rfcaces.encore.PROGRESSIVE_MODE";

    void setImageHeight(int imageHeight);

    int getImageHeight();

    void setImageWidth(int imageWidth);

    int getImageWidth();

    String getEncoderMimeType();

    void setEncoderMimeType(String mimeType);

    String getEncoderSuffix();

    void setEncoderSuffix(String suffix);
}
