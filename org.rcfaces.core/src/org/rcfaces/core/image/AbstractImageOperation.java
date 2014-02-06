/*
 * $Id: AbstractImageOperation.java,v 1.2 2013/07/03 12:25:08 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.image;

import org.rcfaces.core.internal.content.AbstractBufferOperation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:08 $
 */
public abstract class AbstractImageOperation extends AbstractBufferOperation
        implements IImageOperation {
    

    private String responseSuffix;

    private String responseMimeType;

    private String sourceMimeType;

    private String encoderMimeType;

    public final void setResponseMimeType(String responseMimeType) {
        this.responseMimeType = responseMimeType;
    }

    public final void setSourceMimeType(String sourceMimeType) {
        this.sourceMimeType = sourceMimeType;
    }

    public final void setEncoderMimeType(String encoderMimeType) {
        this.encoderMimeType = encoderMimeType;
    }

    public final void setResponseSuffix(String responseSuffix) {
        this.responseSuffix = responseSuffix;
    }

    public void prepare(IImageOperationContentModel imageOperationContentModel,
            IGenerationResourceInformation generationInformation,
            IGeneratedImageInformation generatedInformation) {
        if (sourceMimeType != null) {
            generatedInformation.setSourceMimeType(sourceMimeType);
        }

        if (responseMimeType != null) {
            generatedInformation.setResponseMimeType(responseMimeType);
        }

        if (encoderMimeType != null) {
            generatedInformation.setEncoderMimeType(encoderMimeType);
        }

        if (responseSuffix != null) {
            generatedInformation.setResponseSuffix(responseSuffix);
        }
    }
}
