/*
 * $Id: ImageContentAccessorHandler.java,v 1.3 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.internal.images;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.image.GeneratedImageInformation;
import org.rcfaces.core.image.IGeneratedImageInformation;
import org.rcfaces.core.image.IImageOperation;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.BasicContentAccessor;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorsRegistryImpl;
import org.rcfaces.core.internal.contentAccessor.FiltredContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessorHandler;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:08 $
 */
public abstract class ImageContentAccessorHandler extends AbstractProvider
        implements IContentAccessorHandler {
    

    private static final Log LOG = LogFactory
            .getLog(ImageContentAccessorHandler.class);

    public static final String IMAGE_CONTENT_PROVIDER_ID = "org.rcfaces.core.IMAGE_CONTENT_PROVIDER";

    public abstract IImageOperation getImageOperation(String operationId);

    public abstract IImageResourceAdapter[] listImageResourceAdapters(
            String contentType, String suffix);

    public ImageContentAccessorHandler() {
    }

    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        ((ContentAccessorsRegistryImpl) rcfacesContext
                .getContentAccessorRegistry()).declareContentAccessorHandler(
                IContentFamily.IMAGE, this);
    }

    protected abstract IContentAccessor formatImageURL(
            FacesContext facesContext, IContentAccessor contentAccessor,
            IGeneratedImageInformation generatedImageInformation,
            IGenerationResourceInformation generationInformation);

    public IContentAccessor handleContent(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformationRef,
            IGenerationResourceInformation generationInformation) {

        // if (contentAccessor.getPathType() !=
        // IContentAccessor.FILTER_PATH_TYPE) {
        // return null;
        // }

        Object content = contentAccessor.getContentRef();
        if ((content instanceof String) == false) {
            return null;
        }

        if (isProviderEnabled() == false) {
            if (LOG.isDebugEnabled()) {
                LOG
                        .debug("Provider is disabled, return an unsupported content accessor flag");
            }

            if (contentAccessor.getPathType() == IContentPath.FILTER_PATH_TYPE) {
                return ContentAccessorFactory.UNSUPPORTED_CONTENT_ACCESSOR;
            }

            return null;
        }

        String url = (String) content;

        GeneratedImageInformation imageGeneratedInformation = null;
        IGeneratedResourceInformation generatedInformation = contentInformationRef[0];
        if (generatedInformation instanceof GeneratedImageInformation) {
            imageGeneratedInformation = (GeneratedImageInformation) generatedInformation;

        } else {
            imageGeneratedInformation = new GeneratedImageInformation();

            contentInformationRef[0] = imageGeneratedInformation;
        }

        if (generationInformation == null) {
            generationInformation = new BasicGenerationResourceInformation();
        }
        generationInformation.setAttribute(
                IGenerationResourceInformation.SOURCE_KEY, url);

        IContentAccessor modifiedContentAccessor = contentAccessor;

        if (contentAccessor.getPathType() == IContentPath.FILTER_PATH_TYPE) {

            int idx = url.indexOf(IContentAccessor.FILTER_SEPARATOR);
            String filter = url.substring(0, idx);

            if (idx == url.length() - 2) { // Filtre tout seul !
                IContentAccessor parentAccessor = contentAccessor
                        .getParentAccessor();

                if (parentAccessor == null) {
                    throw new FacesException("Can not get main image of '"
                            + url + "'.");
                }

                modifiedContentAccessor = new FiltredContentAccessor(filter,
                        parentAccessor);

            } else {
                String newURL = url.substring(idx
                        + IContentAccessor.FILTER_SEPARATOR.length());

                modifiedContentAccessor = new FiltredContentAccessor(filter,
                        new BasicContentAccessor(facesContext, newURL,
                                contentAccessor,
                                IContentPath.UNDEFINED_PATH_TYPE));
            }
        }

        IContentAccessor formattedContentAccessor = formatImageURL(
                facesContext, modifiedContentAccessor,
                imageGeneratedInformation, generationInformation);

        if (LOG.isDebugEnabled()) {
            LOG.debug("formattedContentAccessor=" + formattedContentAccessor);
        }

        return formattedContentAccessor;
    }

    public abstract boolean isProviderEnabled();

    public abstract String getMimeType(String url);

    public abstract int getValidContenType(String contentType);

    public static boolean isOperationSupported(FacesContext facesContext,
            String operationId, IContentAccessor imageContentAccessor) {
        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        ImageContentAccessorHandler imageOperationRepository = (ImageContentAccessorHandler) rcfacesContext
                .getProvidersRegistry().getProvider(
                        ImageContentAccessorHandler.IMAGE_CONTENT_PROVIDER_ID);

        if (imageOperationRepository == null) {
            return false;
        }

        return imageOperationRepository.isOperationSupported(operationId,
                imageContentAccessor);
    }

    protected abstract boolean isOperationSupported(String operationId,
            IContentAccessor imageContentAccessor);
}
