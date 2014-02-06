/*
 * $Id: ImageOperationContentModel.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.image.IGeneratedImageInformation;
import org.rcfaces.core.image.IGenerationImageInformation;
import org.rcfaces.core.image.IImageIOOperation;
import org.rcfaces.core.image.IImageOperation;
import org.rcfaces.core.image.IImageOperationContentModel;
import org.rcfaces.core.image.IIndexedImageOperation;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.content.AbstractOperationContentModel;
import org.rcfaces.core.internal.content.IBufferOperation;
import org.rcfaces.core.internal.content.IFileBuffer;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory.IResourceLoader;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ImageOperationContentModel extends AbstractOperationContentModel
        implements IImageOperationContentModel {

    private static final long serialVersionUID = 3641020501370064750L;

    private static final Log LOG = LogFactory
            .getLog(ImageOperationContentModel.class);

    private transient ImageContentAccessorHandler imageContentAccessorHandler;

    public ImageOperationContentModel(String resourceURL, String versionId,
            String operationId, String filterParametersToParse,
            IBufferOperation bufferOperation, String specifiedResourceKey) {
        super(resourceURL, versionId, operationId, filterParametersToParse,
                bufferOperation, specifiedResourceKey);
    }

    @Override
    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        super.setInformations(generationInformation, generatedInformation);

        if (generatedInformation.isProcessingAtRequestSetted() == false) {
            generatedInformation.setProcessingAtRequest(true);
        }
    }

    protected ImageContentAccessorHandler getImageContentAccessorHandler(
            FacesContext facesContext) {
        if (imageContentAccessorHandler != null) {
            return imageContentAccessorHandler;
        }

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        imageContentAccessorHandler = (ImageContentAccessorHandler) rcfacesContext
                .getProvidersRegistry().getProvider(
                        ImageContentAccessorHandler.IMAGE_CONTENT_PROVIDER_ID);

        return imageContentAccessorHandler;
    }

    @Override
    protected IBufferOperation createBufferOperation(FacesContext facesContext) {

        IImageOperation imageOperation = getImageContentAccessorHandler(
                facesContext).getImageOperation(getOperationId());

        return imageOperation;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected IFileBuffer createFileBuffer() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        IImageOperation imageOperation = (IImageOperation) getBufferOperation(facesContext);
        if (imageOperation == null) {
            LOG.error("Can not get image operation associated to id '"
                    + getOperationId() + "'.");
            return INVALID_BUFFERED_FILE;
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        HttpServletResponse response = (HttpServletResponse) externalContext
                .getResponse();
        ServletContext servletContext = (ServletContext) externalContext
                .getContext();

        IGeneratedImageInformation generatedImageInformation = (IGeneratedImageInformation) generatedInformation;

        imageOperation.prepare(this, generationInformation,
                generatedImageInformation);

        String sourceContentType = generatedImageInformation
                .getSourceMimeType();

        IResourceLoaderFactory resourceLoaderFactory = getResourceLoaderFactory(facesContext);

        IResourceLoader resourceLoader = resourceLoaderFactory.loadResource(
                servletContext, request, response, getResourceURL());

        String resourceLoaderContentType = resourceLoader.getContentType();
        if (resourceLoaderContentType == null
                || resourceLoaderContentType.equals(sourceContentType) == false) {
            LOG.error("Different content types request='" + getContentType()
                    + "' loaded='" + sourceContentType + "' for path '"
                    + getResourceURL() + "'.");

            // return INVALID_BUFFERED_FILE;
            // C'est peut etre normal (ex: svg => jpeg)
        }

        InputStream inputStream = resourceLoader.openStream();

        if (inputStream == null) {
            LOG.error("Can not get image specified by path '"
                    + getResourceURL() + "'.");

            return INVALID_BUFFERED_FILE;
        }

        String responseMimeType = generatedImageInformation
                .getResponseMimeType();

        String encoderMimeType = generatedImageInformation.getEncoderMimeType();
        if (encoderMimeType == null) {
            encoderMimeType = responseMimeType;

            if (encoderMimeType == null) {
                encoderMimeType = sourceContentType;
            }
        }

        if (responseMimeType == null) {
            responseMimeType = encoderMimeType;
            generatedImageInformation.setResponseMimeType(responseMimeType);
        }

        Iterator<ImageWriter> it = ImageIO
                .getImageWritersByMIMEType(encoderMimeType);
        if (it.hasNext() == false) {
            LOG.error("Can not write image for mime type '" + encoderMimeType
                    + "'.");

            return INVALID_BUFFERED_FILE;
        }

        ImageWriter imageWriter = it.next();

        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        setupWriteParam(generationInformation, imageWriteParam);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Use imageWriter='" + imageWriter + "' for mimeType='"
                    + encoderMimeType + "'");
        }

        BufferedImage image = readImage(facesContext, inputStream,
                sourceContentType);
        if (image == null) {
            return INVALID_BUFFERED_FILE;
        }

        int sourceImageType = image.getType();
        if (sourceImageType == BufferedImage.TYPE_BYTE_BINARY) {
            sourceImageType = BufferedImage.TYPE_BYTE_INDEXED;
        }

        if ("image/bmp".equals(sourceContentType)) {
            // sourceImageType = BufferedImage.TYPE_4BYTE_ABGR;
        }

        IBufferedImage bufferedImage = createNewBufferedImage(getResourceURL());

        RenderedImage renderedImage;
        try {
            renderedImage = filter(image,
                    new IImageOperation[] { imageOperation },
                    new Map[] { getFilterParameters() });

            try {
                bufferedImage.initialize(resourceLoader, responseMimeType,
                        renderedImage, imageWriter, imageWriteParam,
                        sourceImageType, resourceLoader.getLastModified());

            } catch (IOException e) {
                LOG.error("Can not create filtred image '" + getResourceURL()
                        + "'.", e);

                return INVALID_BUFFERED_FILE;
            }

        } finally {
            imageWriter.dispose();
        }

        if (renderedImage != null) {
            generatedImageInformation.setImageWidth(renderedImage.getWidth());
            generatedImageInformation.setImageHeight(renderedImage.getHeight());
        }

        return bufferedImage;
    }

    public static void setupWriteParam(
            IGenerationResourceInformation generationInformation,
            ImageWriteParam imageWriteParam) {

        Integer compressionMode = (Integer) generationInformation
                .getAttribute(IGenerationImageInformation.COMPRESSION_MODE);
        if (compressionMode != null) {
            imageWriteParam.setCompressionMode(compressionMode.intValue());
        }

        Float quality = (Float) generationInformation
                .getAttribute(IGenerationImageInformation.COMPRESSION_QUALITY);
        if (quality != null) {
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionQuality(quality.floatValue());
        }

        String compressionType = (String) generationInformation
                .getAttribute(IGenerationImageInformation.COMPRESSION_TYPE);
        if (compressionType != null) {
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionType(compressionType);
        }

        Boolean progressiveMode = (Boolean) generationInformation
                .getAttribute(IGenerationImageInformation.COMPRESSION_PROGRESSIVE_MODE);
        if (progressiveMode != null) {
            imageWriteParam
                    .setProgressiveMode((progressiveMode.booleanValue()) ? ImageWriteParam.MODE_COPY_FROM_METADATA
                            : ImageWriteParam.MODE_DISABLED);
        }
    }

    private BufferedImage readImage(FacesContext facesContext,
            InputStream inputStream, String sourceContentType) {

        String suffix = getURLSuffix();

        IImageResourceAdapter imageResourceAdapters[] = getImageContentAccessorHandler(
                facesContext).listImageResourceAdapters(sourceContentType,
                suffix);

        if (imageResourceAdapters != null && imageResourceAdapters.length > 0) {

            for (int i = 0; i < imageResourceAdapters.length; i++) {
                IImageResourceAdapter imageResourceAdapter = imageResourceAdapters[i];

                if ((imageResourceAdapter instanceof IImageIOResourceAdapter) == false) {
                    continue;
                }

                BufferedImage image = ((IImageIOResourceAdapter) imageResourceAdapter)
                        .adaptContent(facesContext, inputStream,
                                generationInformation, generatedInformation);

                if (image != null) {
                    return image;
                }
            }
        }

        try {
            Iterator<ImageReader> it = ImageIO
                    .getImageReadersByMIMEType(sourceContentType);

            if (it.hasNext() == false) {
                throw new IOException("Can not get codec to read image '"
                        + getResourceURL() + "'. (type '" + sourceContentType
                        + "')");
            }

            ImageReader imageReader = it.next();

            try {
                ImageInputStream imageInputStream = ImageIO
                        .createImageInputStream(inputStream);

                try {
                    ImageReadParam param = imageReader.getDefaultReadParam();
                    imageReader.setInput(imageInputStream, true, true);

                    return imageReader.read(0, param);

                } finally {
                    imageInputStream.close();
                }

            } finally {
                imageReader.dispose();
            }

        } catch (Exception e) {
            LOG.error("Can not load image '" + getResourceURL() + "'.", e);

            return null;

        } finally {
            try {
                inputStream.close();

            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    protected IBufferedImage createNewBufferedImage(String imageName) {
        return new FileRenderedImage(imageName);
    }

    @Override
    protected IResourceLoaderFactory getResourceLoaderFactory(
            FacesContext facesContext) {

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        IResourceLoaderFactory imageLoaderFactory;
        if (rcfacesContext.isDesignerMode()) {
            imageLoaderFactory = Constants.getDesignerImageLoaderFactory();

        } else {
            imageLoaderFactory = Constants.getImageLoaderFactory();
        }

        return imageLoaderFactory;
    }

    protected RenderedImage filter(BufferedImage image,
            IImageOperation imageOperations[], Map<String, Object>[] parameters) {

        BufferedImage workImage = null;

        IndexColorModel workingColorModel = null;

        if (LOG.isTraceEnabled()) {
            LOG.trace("Process " + imageOperations.length + " image operation"
                    + ((imageOperations.length > 1) ? "s" : "")
                    + ", source image type=" + image.getType() + ".");
        }

        for (int i = 0; i < imageOperations.length; i++) {
            IImageOperation imageOperation = imageOperations[i];

            if ((imageOperation instanceof IImageIOOperation) == false) {
                continue;
            }

            int imageType = image.getType();

            if (LOG.isTraceEnabled()) {
                LOG.trace("Process image operation #" + i + " '"
                        + imageOperation.getName() + "', current image type="
                        + imageType);
            }

            if (imageType == BufferedImage.TYPE_BYTE_INDEXED
                    || imageType == BufferedImage.TYPE_BYTE_BINARY) {
                IndexColorModel indexColorModel = (IndexColorModel) image
                        .getColorModel();

                int support = IIndexedImageOperation.INDEX_COLOR_MODEL_NOT_SUPPORTED;
                if (imageOperation instanceof IIndexedImageOperation) {
                    support = ((IIndexedImageOperation) imageOperation)
                            .indexedColorModelSupport();
                }

                if (support == IIndexedImageOperation.INDEX_COLOR_MODEL_COLORS_MAP) {
                    if (workingColorModel == null) {
                        workingColorModel = indexColorModel;
                    }

                    workingColorModel = ((IIndexedImageOperation) imageOperation)
                            .filter(parameters[i], indexColorModel, image);

                    continue;

                } else if (support == IIndexedImageOperation.INDEX_COLOR_MODEL_NOT_SUPPORTED) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Image operation '"
                                + imageOperation.getName()
                                + "' does not support current color model : convert image to ARGB.");
                    }

                    if (workingColorModel != null) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("WorkingColorModel is defined, convert image to model.");
                        }

                        image = new BufferedImage(workingColorModel,
                                image.getRaster(), false, null);
                        workingColorModel = null;
                    }

                    BufferedImage rgbImage = new BufferedImage(
                            image.getWidth(), image.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = rgbImage.createGraphics();
                    try {
                        g.drawImage(image, 0, 0, null);

                    } finally {
                        g.dispose();
                    }

                    image = rgbImage;
                }
            }

            if (workingColorModel != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("WorkingColorModel is defined, convert image to model.");
                }

                image = new BufferedImage(workingColorModel, image.getRaster(),
                        false, null);
                workingColorModel = null;
            }

            if (workImage == null) {
                workImage = new BufferedImage(image.getWidth(),
                        image.getHeight(), image.getType());
            }

            image = ((IImageIOOperation) imageOperation).filter(parameters[i],
                    image, workImage);
            workImage = null;
        }

        if (workingColorModel != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("WorkingColorModel is defined, convert image to model.");
            }

            image = new BufferedImage(workingColorModel, image.getRaster(),
                    false, null);
        }

        return image;
    }
}