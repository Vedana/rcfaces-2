/*
 * $Id: StyleContentAccessorHandler.java,v 1.2 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.AbstractCompositeContentAccessorHandler;
import org.rcfaces.core.internal.contentAccessor.BasicContentAccessor;
import org.rcfaces.core.internal.contentAccessor.BasicGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorsRegistryImpl;
import org.rcfaces.core.internal.contentAccessor.FiltredContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IFiltredContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.contentProxy.IResourceProxyHandler;
import org.rcfaces.core.internal.contentStorage.ContentStorageServlet;
import org.rcfaces.core.internal.contentStorage.IContentStorageEngine;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.style.Constants;
import org.rcfaces.core.internal.style.IStyleContentAccessorHandler;
import org.rcfaces.core.internal.style.IStyleOperation;
import org.rcfaces.core.internal.style.IStyleParser;
import org.rcfaces.core.internal.util.PathTypeTools;
import org.rcfaces.core.internal.version.IResourceVersionHandler;
import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.model.IContentModel;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.renderer.IFrameworkResourceGenerationInformation;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:04 $
 */
public class StyleContentAccessorHandler extends
        AbstractCompositeContentAccessorHandler implements
        IStyleContentAccessorHandler {

    private static final Log LOG = LogFactory
            .getLog(StyleContentAccessorHandler.class);

    private static final String CSS_PARSER_SERVICE_ID = "org.rcfaces.css.CSS_PARSER";

    private static final String CSS_PARSER_ENABLED = Constants
            .getPackagePrefix() + ".MERGE_STYLE_FILES";

    private static final String VERSION_FILTER_NAME = "version";

    private static final String MERGE_FILTER_NAME = IStyleContentAccessorHandler.MERGE_FILTER_NAME;

    private static final String PROCESS_FILTER_NAME = IStyleContentAccessorHandler.PROCESS_FILTER_NAME;

    private final Map<String, IStyleOperation> operationsById = new HashMap<String, IStyleOperation>(
            32);

    private final FileNameMap fileNameMap;

    private IStyleParser cssParser = null;

    private final Object contentAccessorAvailable_LOCK = new Object();

    private volatile Boolean contentAccessorAvailable;

    private RcfacesContext rcfacesContext;

    private IResourceVersionHandler resourceVersionHandler = null;

    public StyleContentAccessorHandler() {
        fileNameMap = URLConnection.getFileNameMap();

        operationsById.put(MERGE_FILTER_NAME, new MergeLinkedStylesOperation());
        operationsById.put(PROCESS_FILTER_NAME, new ProcessStylesOperation());
        operationsById.put(VERSION_FILTER_NAME,
                new VersionLinkedStylesOperation());
    }

    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        rcfacesContext = RcfacesContext.getInstance(facesContext);

        if ("false".equalsIgnoreCase(facesContext.getExternalContext()
                .getInitParameter(CSS_PARSER_ENABLED)) == false) {

            cssParser = (IStyleParser) rcfacesContext.getProvidersRegistry()
                    .getProvider(CSS_PARSER_SERVICE_ID);

            if (cssParser != null) {
                ((ContentAccessorsRegistryImpl) rcfacesContext
                        .getContentAccessorRegistry())
                        .declareContentAccessorHandler(IContentFamily.STYLE,
                                this);
            }
        }

        IResourceVersionHandler resourceVersionHandler = (IResourceVersionHandler) rcfacesContext
                .getProvidersRegistry().getProvider(IResourceVersionHandler.ID);
        if (resourceVersionHandler != null
                && resourceVersionHandler.isEnabled()) {
            this.resourceVersionHandler = resourceVersionHandler;
        }

    }

    public String getId() {
        return "StyleContentAccessor";
    }

    public IContentAccessor handleContent(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] generatedInformationRef,
            IGenerationResourceInformation generationInformation) {

        int pathType = contentAccessor.getPathType();

        if (pathType != IContentPath.FILTER_PATH_TYPE) {

            // On verifie si la "versionalisation" n'est pas activée

            if (resourceVersionHandler == null
                    || (pathType != IContentPath.CONTEXT_PATH_TYPE && pathType != IContentPath.RELATIVE_PATH_TYPE)) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Path type not supported '" + pathType + "' ("
                            + contentAccessor.getContentRef() + ")");
                }

                return null;
            }
        }

        Object content = contentAccessor.getContentRef();
        if ((content instanceof String) == false) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ContentRef is not a String (" + content + ")");
            }
            return null;
        }

        if (isProviderEnabled(facesContext) == false) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Provider is disabled, return an unsupported content accessor flag");
            }

            return ContentAccessorFactory.UNSUPPORTED_CONTENT_ACCESSOR;
        }

        String url = (String) content;

        IFiltredContentAccessor modifiedContentAccessor = null;

        int idx = url.indexOf(IContentAccessor.FILTER_SEPARATOR);

        boolean frameworkResource = false;
        if (generationInformation instanceof IFrameworkResourceGenerationInformation) {
            frameworkResource = ((IFrameworkResourceGenerationInformation) generationInformation)
                    .isFrameworkResource();
        }

        String filter;
        String newURL;
        int newUrlPathType;
        if (idx < 0) {
            if (frameworkResource) {
                filter = MERGE_FILTER_NAME;
            } else {
                filter = VERSION_FILTER_NAME;
            }
            newURL = url;
            newUrlPathType = pathType;

        } else if (idx == url.length() - 2) { // Filtre tout seul !
            filter = url.substring(0, idx);
            newUrlPathType = IContentPath.FILTER_PATH_TYPE;
            // throw new FacesException("You can not specify a filter only.");
            newURL = null;

        } else {
            filter = url.substring(0, idx);
            newURL = url.substring(idx
                    + IContentAccessor.FILTER_SEPARATOR.length());
            newUrlPathType = IContentPath.UNDEFINED_PATH_TYPE;
        }

        modifiedContentAccessor = new FiltredContentAccessor(filter,
                new BasicContentAccessor(facesContext, newURL, contentAccessor,
                        newUrlPathType));

        if (generationInformation == null) {
            generationInformation = new BasicGenerationResourceInformation();
        }

        generationInformation.setAttribute(
                IGenerationResourceInformation.SOURCE_KEY, url);

        IContentAccessor formattedContentAccessor = formatStyleURL(
                facesContext, modifiedContentAccessor, generatedInformationRef,
                generationInformation);

        if (LOG.isDebugEnabled()) {
            LOG.debug("formattedContentAccessor=" + formattedContentAccessor);
        }

        return formattedContentAccessor;
    }

    public IContentAccessor formatStyleURL(FacesContext facesContext,
            IFiltredContentAccessor contentAccessor,
            IGeneratedResourceInformation generatedResourceInformationRef[],
            IGenerationResourceInformation generationResourceInformation) {

        String filter = contentAccessor.getFilter();
        String operationId = filter;
        String parameters = null;
        int pf = operationId.indexOf('(');
        if (pf >= 0) {
            int pfe = operationId.lastIndexOf(')');
            if (pfe < 0) {
                parameters = operationId.substring(pf + 1);
            } else {
                parameters = operationId.substring(pf + 1, pfe);
            }

            operationId = operationId.substring(0, pf);
        }

        IStyleOperation styleOperation = getStyleOperation(operationId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Style operation id='" + operationId + "' filter='"
                    + contentAccessor.getFilter() + "' => " + styleOperation);
        }

        String resourceURL = (String) contentAccessor.getContentRef();
        int resourcePathType = contentAccessor.getPathType();

        if (resourceURL == null) {
            IContentAccessor rootAccessor = contentAccessor.getParentAccessor();
            resourceURL = (String) rootAccessor.getContentRef();
            resourcePathType = rootAccessor.getPathType();
        }

        String contentType = getContentType(resourceURL);

        if (isValidContenType(contentType) == false) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not supported content type '" + contentType
                        + "' for url '" + contentAccessor + "'.");
            }

            if (contentAccessor.getParentAccessor() == null) {
                return null;
            }
        }

        IContentStorageEngine contentStorageEngine = rcfacesContext
                .getContentStorageEngine();

        // Il nous faut un path en relatif context !
        switch (resourcePathType) {
        case IContentPath.EXTERNAL_PATH_TYPE:
            throw new FacesException(
                    "Can not make operation on an external URL !");

        case IContentPath.CONTEXT_PATH_TYPE:
            break;

        case IContentPath.ABSOLUTE_PATH_TYPE:
            String relativeURL = PathTypeTools
                    .convertAbsolutePathToContextType(facesContext, resourceURL);

            if (relativeURL == null) {
                throw new FacesException(
                        "Can not transform Absolute path to Context path !");
            }

            resourceURL = relativeURL;
            break;

        case IContentPath.RELATIVE_PATH_TYPE:
            IProcessContext processContext = AbstractProcessContext
                    .getProcessContext(facesContext);

            resourceURL = processContext.getAbsolutePath(resourceURL, false);
            break;

        default:
            if (resourceURL != null) {
                throw new FacesException("Invalid state !");
            }
        }

        String versionId = null;
        if (resourceURL != null
                && org.rcfaces.core.internal.Constants.RESOURCE_CONTENT_VERSION_SUPPORT) {
            IResourceVersionHandler resourceVersionHandler = rcfacesContext
                    .getResourceVersionHandler();

            if (resourceVersionHandler != null) {
                versionId = resourceVersionHandler.getResourceVersion(
                        facesContext, resourceURL, null);
            }
        }

        IContentModel contentModel = new CssOperationContentModel(resourceURL,
                versionId, operationId, parameters, styleOperation, cssParser,
                resourceVersionHandler, null);

        if (generatedResourceInformationRef[0] == null) {
            generatedResourceInformationRef[0] = new BasicGeneratedResourceInformation();
        }
        generatedResourceInformationRef[0]
                .setResponseMimeType(IHtmlRenderContext.CSS_TYPE);

        IContentAccessor newContentAccessor = contentStorageEngine
                .registerContentModel(facesContext, contentModel,
                        generatedResourceInformationRef[0],
                        generationResourceInformation);

        // pas de versionning dans ce content Accessor !

        IResourceProxyHandler resourceProxyHandler = rcfacesContext
                .getResourceProxyHandler();
        if (resourceProxyHandler != null && resourceProxyHandler.isEnabled()
                && resourceProxyHandler.isFiltredResourcesEnabled()) {
            newContentAccessor.setContentProxyHandler(contentAccessor
                    .getContentProxyHandler());
        }

        return newContentAccessor;
    }

    public String getContentType(String url) {
        if (url == null) {
            return ExtendedHttpServlet.CSS_MIME_TYPE;
        }

        int idx = url.lastIndexOf('/');
        if (idx >= 0) {
            url = url.substring(idx + 1);
        }

        String typeMime = fileNameMap.getContentTypeFor(url);
        if (typeMime != null) {
            return typeMime;
        }

        if (url.toLowerCase().endsWith(".css")) {
            return ExtendedHttpServlet.CSS_MIME_TYPE;
        }

        return null;
    }

    public boolean isProviderEnabled(FacesContext facesContext) {
        if (contentAccessorAvailable != null) {
            return contentAccessorAvailable.booleanValue();
        }

        synchronized (contentAccessorAvailable_LOCK) {
            contentAccessorAvailable = Boolean.valueOf(ContentStorageServlet
                    .getContentStorageBaseURI(facesContext.getExternalContext()
                            .getApplicationMap()) != null);
        }

        if (contentAccessorAvailable.booleanValue() == false) {
            LOG.info("StyleContentAccessor is not available");

        } else {
            LOG.debug("StyleContentAccessor available");
        }

        return contentAccessorAvailable.booleanValue();
    }

    public boolean isValidContenType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("text/css");
    }

    public IStyleOperation getStyleOperation(String operationId) {
        IStyleOperation styleOperation = operationsById.get(operationId);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Operation id='" + operationId + "' => " + styleOperation);
        }

        return styleOperation;
    }
}
