/*
 * $Id: ScriptContentAccessorHandler.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.script;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
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
import org.rcfaces.core.internal.contentStorage.ContentStorageServlet;
import org.rcfaces.core.internal.contentStorage.IContentStorageEngine;
import org.rcfaces.core.internal.images.ImageContentAccessorHandler;
import org.rcfaces.core.internal.script.AbstractScriptContentAccessorHandler;
import org.rcfaces.core.internal.script.IScriptOperation;
import org.rcfaces.core.internal.style.Constants;
import org.rcfaces.core.internal.util.PathTypeTools;
import org.rcfaces.core.internal.version.IResourceVersionHandler;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.model.IContentModel;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
public class ScriptContentAccessorHandler extends
        AbstractScriptContentAccessorHandler {
  

    private static final Log LOG = LogFactory
            .getLog(ImageContentAccessorHandler.class);

    private static final String MERGE_SCRIPT_ENABLED = Constants
            .getPackagePrefix() + ".MERGE_SCRIPT_FILES";

    private boolean contentAccessorAvailable;

    private RcfacesContext rcfacesContext;

    private final Map<String, IScriptOperation> operationsById = new HashMap<String, IScriptOperation>(
            32);

    private final FileNameMap fileNameMap;

    public ScriptContentAccessorHandler() {
        fileNameMap = URLConnection.getFileNameMap();

        operationsById.put(COMPOSITE_OPERATION_ID,
                new CompositeScriptsContentOperation());
    }

    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        rcfacesContext = RcfacesContext.getInstance(facesContext);

        if ("true".equalsIgnoreCase(facesContext.getExternalContext()
                .getInitParameter(MERGE_SCRIPT_ENABLED))) {

            ((ContentAccessorsRegistryImpl) rcfacesContext
                    .getContentAccessorRegistry())
                    .declareContentAccessorHandler(IContentFamily.SCRIPT, this);
        }

        contentAccessorAvailable = ContentStorageServlet
                .getContentStorageBaseURI(facesContext.getExternalContext()
                        .getApplicationMap()) != null;

        if (contentAccessorAvailable == false) {
            LOG.info("ScriptContentAccessor is not available");

        } else {
            LOG.debug("ScriptContentAccessor available");
        }
    }

    public String getId() {
        return "ScriptContentAccessor";
    }

    public IContentAccessor handleContent(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] generatedInformationRef,
            IGenerationResourceInformation generationInformation) {

        if (contentAccessor.getPathType() != IContentPath.FILTER_PATH_TYPE) {
            return null;
        }

        Object content = contentAccessor.getContentRef();
        if ((content instanceof String) == false) {
            return null;
        }

        if (isProviderEnabled() == false) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Provider is disabled, return an unsupported content accessor flag");
            }

            return ContentAccessorFactory.UNSUPPORTED_CONTENT_ACCESSOR;
        }

        String url = (String) content;

        IFiltredContentAccessor modifiedContentAccessor = null;

        int idx = url.indexOf(IContentAccessor.FILTER_SEPARATOR);
        String filter = url.substring(0, idx);

        if (idx == url.length() - 2) { // Filtre tout seul !
            throw new FacesException("You can not specify a filter only.");
        }

        String newURL = url.substring(idx
                + IContentAccessor.FILTER_SEPARATOR.length());

        modifiedContentAccessor = new FiltredContentAccessor(filter,
                new BasicContentAccessor(facesContext, newURL, contentAccessor,
                        IContentPath.UNDEFINED_PATH_TYPE));

        if (generationInformation == null) {
            generationInformation = new BasicGenerationResourceInformation();
        }
        generationInformation.setAttribute(
                IGenerationResourceInformation.SOURCE_KEY, url);

        IContentAccessor formattedContentAccessor = formatScriptURL(
                facesContext, modifiedContentAccessor, generatedInformationRef,
                generationInformation);

        if (LOG.isDebugEnabled()) {
            LOG.debug("formattedContentAccessor=" + formattedContentAccessor);
        }

        return formattedContentAccessor;
    }

    public IContentAccessor formatScriptURL(FacesContext facesContext,
            IFiltredContentAccessor contentAccessor,
            IGeneratedResourceInformation[] generatedResourceInformationRef,
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

        IScriptOperation scriptOperation = getScriptOperation(operationId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Script operation id='" + operationId + "' filter='"
                    + contentAccessor.getFilter() + "' => " + scriptOperation);
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

        // Il nous faut un path en relatif !
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
            resourceURL = PathTypeTools.convertRelativePathToContextPath(
                    facesContext, resourceURL, null);
            break;

        default:
            throw new FacesException("Invalid state !");
        }

        String versionId = null;
        if (org.rcfaces.core.internal.Constants.RESOURCE_CONTENT_VERSION_SUPPORT) {
            IResourceVersionHandler resourceVersionHandler = rcfacesContext
                    .getResourceVersionHandler();
            if (resourceVersionHandler != null) {
                versionId = resourceVersionHandler.getResourceVersion(
                        facesContext, resourceURL, null);
            }
        }

        IContentModel contentModel = new ScriptOperationContentModel(
                resourceURL, versionId, operationId, parameters,
                scriptOperation, null);

        if (generatedResourceInformationRef[0] == null) {
            generatedResourceInformationRef[0] = new BasicGeneratedResourceInformation();
        }
        generatedResourceInformationRef[0]
                .setResponseMimeType(IHtmlRenderContext.JAVASCRIPT_TYPE);

        IContentAccessor newContentAccessor = contentStorageEngine
                .registerContentModel(facesContext, contentModel,
                        generatedResourceInformationRef[0],
                        generationResourceInformation);

        // pas de versionning dans ce content Accessor !

        return newContentAccessor;
    }

    public String getContentType(String url) {
        int idx = url.lastIndexOf('/');
        if (idx >= 0) {
            url = url.substring(idx + 1);
        }

        String typeMime = fileNameMap.getContentTypeFor(url);
        if (typeMime != null) {
            return typeMime;
        }

        if (url.toLowerCase().endsWith(".js")) {
            return IHtmlRenderContext.JAVASCRIPT_TYPE;
        }

        return null;
    }

    public boolean isProviderEnabled() {
        return contentAccessorAvailable;
    }

    public boolean isValidContenType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith(IHtmlRenderContext.JAVASCRIPT_TYPE);
    }

    public IScriptOperation getScriptOperation(String operationId) {
        IScriptOperation scriptOperation = operationsById.get(operationId);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Operation id='" + operationId + "' => "
                    + scriptOperation);
        }

        return scriptOperation;
    }

    public boolean isOperationSupported(String operationId,
            IContentAccessor contentAccessor) {
        return operationsById.containsKey(operationId);
    }
}
