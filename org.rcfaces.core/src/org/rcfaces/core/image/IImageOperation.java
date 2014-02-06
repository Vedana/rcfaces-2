/*
 * $Id: IImageOperation.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 * 
 */
package org.rcfaces.core.image;

import org.rcfaces.core.internal.content.IBufferOperation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IImageOperation extends IBufferOperation {

    void setResponseSuffix(String suffix);

    void setResponseMimeType(String responseMimeType);

    void setSourceMimeType(String sourceMimeType);

    void setEncoderMimeType(String encoderMimeType);

    void prepare(IImageOperationContentModel imageOperationContentModel,
            IGenerationResourceInformation generationInformation,
            IGeneratedImageInformation generatedInformation);
}
