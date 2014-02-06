/*
 * $Id: ImageContentModel.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.image;

import java.awt.image.BufferedImage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.model.BasicContentModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ImageContentModel extends BasicContentModel implements
        IImageContentModel {
    private static final String REVISION = "$Revision: 1.2 $";

    private static final Log LOG = LogFactory.getLog(ImageContentModel.class);

    public static final String IMAGE_WRITE_PARAM_PROPERTY = "javax.imageio.ImageWriteParam";

    public ImageContentModel() {
    }

    public void setCompressionQuality(
            IGenerationResourceInformation imageInformation, float quality) {

        generationInformation.setAttribute(
                IGenerationImageInformation.COMPRESSION_QUALITY, new Float(
                        quality));
    }

    public void setCompressionMode(
            IGenerationResourceInformation imageInformation, int mode) {

        generationInformation
                .setAttribute(IGenerationImageInformation.COMPRESSION_MODE,
                        new Integer(mode));
    }

    public void setCompressionType(
            IGenerationResourceInformation imageInformation,
            String compressionType) {

        generationInformation.setAttribute(
                IGenerationImageInformation.COMPRESSION_TYPE, compressionType);
    }

    public void setProgressiveMode(
            IGenerationResourceInformation imageInformation,
            boolean progressiveMode) {

        generationInformation.setAttribute(
                IGenerationImageInformation.COMPRESSION_PROGRESSIVE_MODE,
                Boolean.valueOf(progressiveMode));
    }

    public Object getWrappedData() {
        Object prev = super.getWrappedData();
        if (prev != null) {
            return prev;
        }

        prev = getBufferedImage();
        setWrappedData(prev);

        return prev;
    }

    protected BufferedImage getBufferedImage() {
        return null;
    }

}
