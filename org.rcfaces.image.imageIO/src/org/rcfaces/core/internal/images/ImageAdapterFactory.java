/*
 * $Id: ImageAdapterFactory.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.image.IGenerationImageInformation;
import org.rcfaces.core.image.ImageContentModel;
import org.rcfaces.core.internal.content.ContentAdapterFactory;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.contentStorage.AdaptationParameters;
import org.rcfaces.core.internal.contentStorage.IResolvedContent;
import org.rcfaces.core.internal.images.operation.GIFConversionImageOperation;
import org.rcfaces.core.internal.images.operation.ICOConversionImageOperation;
import org.rcfaces.core.internal.images.operation.JPEGConversionImageOperation;
import org.rcfaces.core.internal.images.operation.PNGConversionImageOperation;
import org.rcfaces.core.lang.IAdaptable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ImageAdapterFactory extends ContentAdapterFactory {

    private static final Log LOG = LogFactory.getLog(ImageAdapterFactory.class);

    private static final String RGB_DEFAULT_MIME_TYPE = JPEGConversionImageOperation.MIME_TYPES[0];

    private static final String INDEX_DEFAULT_MIME_TYPE = GIFConversionImageOperation.MIME_TYPE;

    private static final String TEMP_PREFIX = "adaptedImage_";

    private static final String IMAGE_TEMP_FOLDER_PARAMETER = "org.rcfaces.images.TEMP_FOLDER";

    static {
        GIFConversionImageOperation.fillSuffixByMimeType(suffixByMimeType);

        JPEGConversionImageOperation.fillSuffixByMimeType(suffixByMimeType);

        PNGConversionImageOperation.fillSuffixByMimeType(suffixByMimeType);

        ICOConversionImageOperation.fillSuffixByMimeType(suffixByMimeType);
    }

    private File tempFolder;

    public ImageAdapterFactory() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext != null) {
            String imageTempFolder = facesContext.getExternalContext()
                    .getInitParameter(IMAGE_TEMP_FOLDER_PARAMETER);
            if (imageTempFolder != null) {
                tempFolder = new File(imageTempFolder);
                if (tempFolder.mkdirs() == false) {
                    LOG.error("Can not create temp folder '" + imageTempFolder
                            + "', ignore it.");
                } else {
                    LOG.info("Set image temp folder to '"
                            + tempFolder.getAbsolutePath() + "'.");
                }
            }
        }
    }

    @Override
    public <T> T getAdapter(Object adaptableObject, Class<T> adapterType,
            Object parameter) {
        if (adaptableObject instanceof RenderedImage) {
            return (T) adaptBufferedImage(adaptableObject, parameter);
        }

        if (adaptableObject instanceof RenderedImage[]) {
            RenderedImage renderedImages[] = (RenderedImage[]) adaptableObject;

            if (renderedImages.length < 1) {
                return null;
            }

            return (T) adaptBufferedImage(renderedImages, parameter);
        }

        return super.getAdapter(adaptableObject, adapterType, parameter);
    }

    @Override
    protected File getTempFolder() {
        if (tempFolder != null) {
            return tempFolder;
        }
        return super.getTempFolder();
    }

    @Override
    protected String getTempPrefix() {
        return TEMP_PREFIX;
    }

    private IResolvedContent adaptBufferedImage(Object image,
            Object adapterParameters) {

        IGenerationResourceInformation generationResourceInformation = null;
        IGeneratedResourceInformation generatedResourceInformation = null;

        if (adapterParameters instanceof AdaptationParameters) {
            AdaptationParameters ap = (AdaptationParameters) adapterParameters;

            generationResourceInformation = ap
                    .getGenerationResourceInformation();
            generatedResourceInformation = ap.getGeneratedResourceInformation();
        }

        Map<String, Object> adapterParametersMap = null;
        if (adapterParameters instanceof Map) {
            adapterParametersMap = (Map<String, Object>) adapterParameters;

        } else if (adapterParametersMap instanceof IAdaptable) {
            adapterParametersMap = (Map<String, Object>) ((IAdaptable) adapterParameters)
                    .getAdapter(Map.class, null);
        }

        String defaultMimeType = RGB_DEFAULT_MIME_TYPE;
        if (image instanceof RenderedImage) {
            defaultMimeType = getDefaultContentType((RenderedImage) image);

        } else if (image instanceof RenderedImage[]) {
            defaultMimeType = getDefaultContentType(((RenderedImage[]) image)[0]);
        }

        String encoderMimeType = null;
        String encoderSuffix = null;
        String responseMimeType = null;
        String responseSuffix = null;

        if (generationResourceInformation != null) {
            if (generationResourceInformation instanceof IGenerationImageInformation) {
                if (encoderMimeType == null) {
                    encoderMimeType = ((IGenerationImageInformation) generationResourceInformation)
                            .getEncoderMimeType();
                }
                if (encoderSuffix == null) {
                    encoderSuffix = ((IGenerationImageInformation) generationResourceInformation)
                            .getEncoderSuffix();
                }
            }
            if (responseMimeType == null) {
                responseMimeType = generationResourceInformation
                        .getResponseMimeType();
            }
            if (responseSuffix == null) {
                responseSuffix = generationResourceInformation
                        .getResponseSuffix();
            }
        }

        if (adapterParametersMap != null) {
            if (encoderMimeType == null) {
                encoderMimeType = (String) adapterParametersMap
                        .get(IGenerationImageInformation.ENCODER_MIME_TYPE_PROPERTY);
            }
            if (encoderSuffix == null) {
                encoderSuffix = (String) adapterParametersMap
                        .get(IGenerationImageInformation.ENCODER_SUFFIX_PROPERTY);
            }

            if (responseMimeType == null) {
                responseMimeType = (String) adapterParametersMap
                        .get(IGenerationResourceInformation.RESPONSE_MIME_TYPE_PROPERTY);
            }
            if (responseSuffix == null) {
                responseSuffix = (String) adapterParametersMap
                        .get(IGeneratedResourceInformation.RESPONSE_URL_SUFFIX_PROPERTY);
            }
        }

        if (responseMimeType != null && encoderMimeType == null) {
            encoderMimeType = responseMimeType;
        }

        if (responseSuffix != null && encoderSuffix == null) {
            encoderSuffix = responseSuffix;
        }

        if (encoderMimeType == null && encoderSuffix != null) {
            encoderMimeType = fileNameMap.getContentTypeFor("x."
                    + encoderSuffix);
        }

        if (encoderMimeType == null) {
            encoderMimeType = defaultMimeType;
        }

        ImageWriteParam imageWriteParam = null;
        if (imageWriteParam == null && generationResourceInformation != null) {
            imageWriteParam = (ImageWriteParam) generationResourceInformation
                    .getAttribute(ImageContentModel.IMAGE_WRITE_PARAM_PROPERTY);
        }

        if (imageWriteParam == null && adapterParametersMap != null) {
            imageWriteParam = (ImageWriteParam) adapterParametersMap
                    .get(ImageContentModel.IMAGE_WRITE_PARAM_PROPERTY);
        }

        String specifiedResourceKey = null;
        if (generationResourceInformation != null) {
            if (generationResourceInformation
                    .getComputeResourceKeyFromGenerationInformation()) {
                specifiedResourceKey = BasicGenerationResourceInformation
                        .generateResourceKeyFromGenerationInformation(generationResourceInformation);
            }
        }

        long responseLastModifiedDate = -1;

        if (responseLastModifiedDate < 0
                && generationResourceInformation != null) {
            responseLastModifiedDate = generationResourceInformation
                    .getResponseLastModified();
        }

        if (responseLastModifiedDate <= 0 && adapterParametersMap != null) {
            Object l = adapterParametersMap
                    .get(IGenerationResourceInformation.RESPONSE_LAST_MODIFIED_PROPERTY);
            if (l instanceof Long) {
                responseLastModifiedDate = ((Long) l).longValue();

            } else if (l instanceof Date) {
                responseLastModifiedDate = ((Date) l).getTime();
            }
        }

        IOException ex = null;

        Iterator<ImageWriter> it = ImageIO
                .getImageWritersByMIMEType(encoderMimeType);
        if (it.hasNext()) {
            ImageWriter imageWriter = it.next();

            try {
                return writeBufferedImage(imageWriter, image, imageWriteParam,
                        encoderMimeType, encoderSuffix, specifiedResourceKey,
                        responseLastModifiedDate);

            } catch (IOException e) {
                ex = e;

            } finally {
                imageWriter.dispose();
            }
        }

        if (encoderMimeType.equals(defaultMimeType) == false) {
            it = ImageIO.getImageWritersByMIMEType(defaultMimeType);
            if (it.hasNext()) {
                ImageWriter imageWriter = it.next();

                try {
                    return writeBufferedImage(imageWriter, image,
                            imageWriteParam, encoderMimeType, encoderSuffix,
                            specifiedResourceKey, responseLastModifiedDate);

                } catch (IOException e) {
                    if (ex == null) {
                        ex = e;
                    }

                } finally {
                    imageWriter.dispose();
                }
            }
        }

        throw new FacesException("Unsupported image mime type '"
                + encoderMimeType + "'.", ex);
    }

    private String getDefaultContentType(RenderedImage image) {
        ColorModel colorModel = image.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            return INDEX_DEFAULT_MIME_TYPE;
        }

        return RGB_DEFAULT_MIME_TYPE;
    }

    private IResolvedContent writeBufferedImage(ImageWriter imageWriter,
            Object image, ImageWriteParam imageWriteParam, String contentType,
            String suffix, String specifiedResourceKey, long lastModifiedDate)
            throws IOException {

        if (suffix == null) {
            suffix = getSuffixByMimeType(contentType);
        }

        File file = createTempFile(contentType, (suffix != null) ? suffix
                : "unknown");

        FileOutputStream fout = new FileOutputStream(file);
        try {
            ImageOutputStream imageOutputStream = ImageIO
                    .createImageOutputStream(fout);

            imageWriter.setOutput(imageOutputStream);

            if (image instanceof RenderedImage) {
                imageWriter.write(null, new IIOImage((RenderedImage) image,
                        null, null), imageWriteParam);

            } else if (image instanceof RenderedImage[]) {
                RenderedImage renderedImages[] = (RenderedImage[]) image;

                if (imageWriter.canWriteSequence() == false) {
                    imageWriter.write(null, new IIOImage(renderedImages[0],
                            null, null), imageWriteParam);
                } else {
                    for (int i = 0; i < renderedImages.length; i++) {
                        imageWriter.write(null, new IIOImage(renderedImages[i],
                                null, null), imageWriteParam);
                    }
                }
            }

            imageWriter.dispose();

            imageOutputStream.close();

        } finally {
            fout.close();
        }

        return new FileResolvedContent(contentType, suffix, file,
                specifiedResourceKey, lastModifiedDate);
    }
}
