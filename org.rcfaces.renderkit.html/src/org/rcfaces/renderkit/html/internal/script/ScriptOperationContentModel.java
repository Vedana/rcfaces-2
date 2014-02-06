/*
 * $Id: ScriptOperationContentModel.java,v 1.2 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.script;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.content.AbstractGzipedBufferOperationContentModel;
import org.rcfaces.core.internal.content.IBufferOperation;
import org.rcfaces.core.internal.content.IFileBuffer;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.script.IScriptContentAccessorHandler;
import org.rcfaces.core.internal.script.IScriptOperation;
import org.rcfaces.core.internal.script.IScriptOperationContext;
import org.rcfaces.core.internal.util.ApplicationParametersMap;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
 */
public class ScriptOperationContentModel extends
        AbstractGzipedBufferOperationContentModel {

    private static final Log LOG = LogFactory
            .getLog(ScriptOperationContentModel.class);

    private static final long serialVersionUID = 8725269579955892798L;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String JAVASCRIPT_SUFFIX = "js";

    public ScriptOperationContentModel(String resourceURL, String versionId,
            String operationId, String filterParametersToParse,
            IScriptOperation scriptOperation, String specifiedResourceKey) {
        super(resourceURL, versionId, operationId, filterParametersToParse,
                scriptOperation, specifiedResourceKey);
    }

    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        super.setInformations(generationInformation, generatedInformation);

        generatedInformation.setResponseSuffix(JAVASCRIPT_SUFFIX);
        generatedInformation
                .setResponseMimeType(IHtmlRenderContext.JAVASCRIPT_TYPE);
    }

    protected IFileBuffer createFileBuffer() {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        IScriptOperation scriptOperation = (IScriptOperation) getBufferOperation(facesContext);
        if (scriptOperation == null) {
            LOG.error("Can not get script operation associated to id '"
                    + getOperationId() + "'.");
            return INVALID_BUFFERED_FILE;
        }

        IResourceLoaderFactory resourceLoaderFactory = getResourceLoaderFactory(facesContext);

        ContentInformation contentInfo[] = new ContentInformation[1];

        String scriptContent = null;
        String resourceURL = getResourceURL();
        scriptContent = loadContent(facesContext, resourceLoaderFactory,
                getResourceURL(), getDefaultCharset(), contentInfo);

        if (scriptContent == null) {
            return INVALID_BUFFERED_FILE;
        }

        Map<String, Object> applicationParameters = new ApplicationParametersMap(
                facesContext);

        IScriptFile scriptFile = createNewScriptFile(resourceURL);
        try {
            IScriptOperationContext parserContext = new ScriptOperationContext(
                    contentInfo[0].getCharSet(),
                    contentInfo[0].getLastModified(), resourceLoaderFactory,
                    applicationParameters);

            String newStyleSheetContent = filter(resourceURL, scriptContent,
                    new IScriptOperation[] { scriptOperation },
                    new Map[] { getFilterParameters() }, parserContext);

            String contentType = getContentType() + "; charset="
                    + parserContext.getCharset();

            scriptFile.initialize(contentType,
                    newStyleSheetContent.getBytes(parserContext.getCharset()),
                    parserContext.getLastModifiedDate());

        } catch (IOException e) {
            LOG.error("Can not create filtred image '" + getResourceURL()
                    + "'.", e);

            return INVALID_BUFFERED_FILE;
        }

        return scriptFile;
    }

    protected String getCharsetFromStream(InputStream inputStream) {
        return DEFAULT_CHARSET;
    }

    protected synchronized IBufferOperation createBufferOperation(
            FacesContext facesContext) {

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        IScriptContentAccessorHandler scriptOperationRepository = (IScriptContentAccessorHandler) rcfacesContext
                .getProvidersRegistry()
                .getProvider(
                        IScriptContentAccessorHandler.SCRIPT_CONTENT_PROVIDER_ID);

        IScriptOperation scriptOperation = scriptOperationRepository
                .getScriptOperation(getOperationId());

        return scriptOperation;
    }

    protected String filter(String scriptURL, String scriptContent,
            IScriptOperation scriptOperations[], Map parameters[],
            IScriptOperationContext scriptOperationContext) throws IOException {

        if (LOG.isTraceEnabled()) {
            LOG.trace("Process " + scriptOperations.length
                    + " script operation"
                    + ((scriptOperations.length > 1) ? "s" : ""));
        }

        for (int i = 0; i < scriptOperations.length; i++) {
            IScriptOperation scriptOperation = scriptOperations[i];

            if (LOG.isTraceEnabled()) {
                LOG.trace("Process script operation #" + i + " '"
                        + scriptOperation.getName() + "'");
            }

            scriptURL = scriptOperation.filter(scriptURL, scriptContent,
                    scriptOperationContext);
        }

        return scriptURL;
    }

    protected String getDefaultCharset() {
        return DEFAULT_CHARSET;
    }

    protected String getDefaultMimeType() {
        return IHtmlRenderContext.JAVASCRIPT_TYPE;
    }

    protected boolean isMimeTypeValid(String contentType) {
        return IHtmlRenderContext.JAVASCRIPT_TYPE.equalsIgnoreCase(contentType);
    }

    private IScriptFile createNewScriptFile(String resourceURL) {
        return new ScriptFileBuffer(resourceURL);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:06 $
     */
    protected static class ScriptOperationContext implements
            IScriptOperationContext {
        private String charset;

        private long lastModifiedDate;

        private final IResourceLoaderFactory resourceLoaderFactory;

        private final Map<String, Object> applicationParameters;

        public ScriptOperationContext(String charset, long lastModifiedDate,
                IResourceLoaderFactory resourceLoaderFactory,
                Map<String, Object> applicationParameters) {
            this.charset = charset;
            this.lastModifiedDate = lastModifiedDate;
            this.resourceLoaderFactory = resourceLoaderFactory;
            this.applicationParameters = applicationParameters;
        }

        public final String getCharset() {
            return charset;
        }

        public final void setCharset(String charset) {
            this.charset = charset;
        }

        public final long getLastModifiedDate() {
            return lastModifiedDate;
        }

        public final void setLastModifiedDate(long lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }

        public IResourceLoaderFactory getResourceLoaderFactory() {
            return resourceLoaderFactory;
        }

        public Map<String, Object> getApplicationParameters() {
            return applicationParameters;
        }

    }
}