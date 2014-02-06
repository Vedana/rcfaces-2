/*
 * $Id: CssOperationContentModel.java,v 1.2 2013/01/11 15:45:04 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.content.AbstractGzipedBufferOperationContentModel;
import org.rcfaces.core.internal.content.IBufferOperation;
import org.rcfaces.core.internal.content.IFileBuffer;
import org.rcfaces.core.internal.content.IOperationContentLoader;
import org.rcfaces.core.internal.contentAccessor.BasicContentAccessor;
import org.rcfaces.core.internal.contentAccessor.BasicContentPath;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.style.IStyleContentAccessorHandler;
import org.rcfaces.core.internal.style.IStyleOperation;
import org.rcfaces.core.internal.style.IStyleParser;
import org.rcfaces.core.internal.style.IStyleParser.IParserContext;
import org.rcfaces.core.internal.util.IPath;
import org.rcfaces.core.internal.util.Path;
import org.rcfaces.core.internal.version.IResourceVersionHandler;
import org.rcfaces.core.internal.version.ResourceVersionHandlerImpl;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:04 $
 */
public class CssOperationContentModel extends
        AbstractGzipedBufferOperationContentModel {

    private static final Log LOG = LogFactory
            .getLog(CssOperationContentModel.class);

    private static final long serialVersionUID = -7274200606212145834L;

    private static final String STYLE_CONTENT_TYPE = "text/css";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String STYLE_SUFFIX = "css";

    private static final int STYLESHEET_BUFFER_INITIAL_SIZE = 8000;

    private final IStyleParser cssParser;

    private final IResourceVersionHandler resourceVersionHandler;

    public CssOperationContentModel(String resourceURL, String versionId,
            String operationId, String filterParametersToParse,
            IStyleOperation styleOperation, IStyleParser cssParser,
            IResourceVersionHandler resourceVersionHandler,
            String specifiedResourceKey) {
        super(resourceURL, versionId, operationId, filterParametersToParse,
                styleOperation, specifiedResourceKey);

        this.cssParser = cssParser;
        this.resourceVersionHandler = resourceVersionHandler;
    }

    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        super.setInformations(generationInformation, generatedInformation);

        generatedInformation.setResponseSuffix(STYLE_SUFFIX);
        generatedInformation.setResponseMimeType(IHtmlRenderContext.CSS_TYPE);
    }

    protected IFileBuffer createFileBuffer() {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        IStyleOperation styleOperation = (IStyleOperation) getBufferOperation(facesContext);
        if (styleOperation == null) {
            LOG.error("Can not get style operation associated to id '"
                    + getOperationId() + "'.");
            return INVALID_BUFFERED_FILE;
        }

        IResourceLoaderFactory resourceLoaderFactory = getResourceLoaderFactory(facesContext);

        String resourceURL = getResourceURL();
        IStyleSheetFile styleSheetFile = createNewStyleSheetFile(resourceURL);

        ContentInformation contentInfo[] = new ContentInformation[1];

        String styleSheetContent = null;

        if (resourceURL != null) {
            styleSheetContent = loadContent(facesContext,
                    resourceLoaderFactory, resourceURL, getDefaultCharset(),
                    contentInfo);
        }

        boolean processRulesEnabled = false;
        if (generationInformation instanceof IProcessRulesGenerationInformation) {
            processRulesEnabled = ((IProcessRulesGenerationInformation) generationInformation)
                    .isProcessRulesEnabled();
        }

        IResourceVersionHandler _resourceVersionHandler = this.resourceVersionHandler;

        if (generationInformation instanceof CssFilesCollectorGenerationInformation) {
            CssFilesCollectorGenerationInformation cs = (CssFilesCollectorGenerationInformation) generationInformation;

            if (cs.isFrameworkResource()) {
                _resourceVersionHandler = null;
            }

            FileItemSource sources[] = cs.listSources();

            if (sources != null && sources.length > 0) {
                StringAppender sa = null;

                IPath path = null;
                if (resourceURL != null) {
                    IPath sourcePath = new Path(resourceURL);
                    IPath parent = new Path("..");
                    path = parent;

                    for (int i = 2; i < sourcePath.segmentCount(); i++) {
                        path = path.append(parent);
                    }
                }

                for (int i = 0; i < sources.length; i++) {
                    FileItemSource source = sources[i];

                    if (source.isFrameworkResource()) {
                        // On desactive le versioning !
                        _resourceVersionHandler = null;
                    }

                    String sourcePath = source.getSource();

                    IContentPath bca = new BasicContentPath(null, sourcePath);
                    String resolvedSourcePath = bca.convertToPathType(
                            facesContext, IContentPath.CONTEXT_PATH_TYPE);

                    if (path != null) {
                        IPath pr = path.append(new Path(resolvedSourcePath));

                        resolvedSourcePath = pr.toString();
                    }

                    if (sa == null) {
                        if (styleSheetContent != null) {
                            sa = new StringAppender(styleSheetContent,
                                    sources.length * 64
                                            + STYLESHEET_BUFFER_INITIAL_SIZE);
                        } else {
                            sa = new StringAppender(sources.length * 64
                                    + STYLESHEET_BUFFER_INITIAL_SIZE);
                        }
                    }

                    sa.append("\n@import url(\"").append(resolvedSourcePath)
                            .append("\");\n");

                }

                if (sa != null) {
                    styleSheetContent = sa.toString();
                }
            }
        }

        if (styleSheetContent != null) {
            try {
                IParserContext parserContext = new ParserContext(facesContext,
                        contentInfo[0].getCharSet(),
                        contentInfo[0].getLastModified(),
                        _resourceVersionHandler, resourceLoaderFactory, this);
                if (processRulesEnabled) {
                    parserContext.enableProcessRules();
                }

                String newStyleSheetContent = filter(cssParser, resourceURL,
                        styleSheetContent,
                        new IStyleOperation[] { styleOperation },
                        new Map[] { getFilterParameters() }, parserContext);

                newStyleSheetContent = "@charset \""
                        + parserContext.getCharset() + "\";\n"
                        + newStyleSheetContent;

                String contentType = getContentType() + "; charset="
                        + parserContext.getCharset();

                styleSheetFile.initialize(contentType, newStyleSheetContent
                        .getBytes(parserContext.getCharset()), parserContext
                        .getLastModifiedDate());

            } catch (IOException e) {
                LOG.error("Can not create filtred image '" + getResourceURL()
                        + "'.", e);

                return INVALID_BUFFERED_FILE;
            }

        } else {
            return INVALID_BUFFERED_FILE;
        }

        return styleSheetFile;
    }

    protected String getCharsetFromStream(InputStream inputStream) {

        BufferedInputStream bins = new BufferedInputStream(inputStream, 128);
        inputStream.mark(128);

        try {
            InputStreamReader reader = new InputStreamReader(bins, "ISO8859-1");

            BufferedReader bufReader = new BufferedReader(reader, 64);

            String line = bufReader.readLine();

            if (line == null) {
                return null;
            }

            line = line.trim();

            String cmd = line.toLowerCase();

            if (cmd.startsWith("@charset") == false) {
                return null;
            }
            cmd = cmd.substring(8).trim();

            int idx = cmd.indexOf(';');
            if (idx > 0) {
                cmd = cmd.substring(0, idx).trim();
            }

            if ((cmd.startsWith("\"") && cmd.endsWith("\""))
                    || (cmd.startsWith("'") && cmd.endsWith("'"))) {
                cmd = cmd.substring(1, cmd.length() - 1).trim();
            }

            return cmd;

        } catch (IOException e) {
            LOG.error("Can not determine charset !", e);
            return null;

        } finally {
            try {
                inputStream.reset();

            } catch (IOException e) {
                LOG.error("Can not reset position !", e);
            }
        }
    }

    protected synchronized IBufferOperation createBufferOperation(
            FacesContext facesContext) {

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        IStyleContentAccessorHandler styleOperationRepository = (IStyleContentAccessorHandler) rcfacesContext
                .getProvidersRegistry().getProvider(
                        IStyleContentAccessorHandler.STYLE_CONTENT_PROVIDER_ID);

        IStyleOperation styleOperation = styleOperationRepository
                .getStyleOperation(getOperationId());

        return styleOperation;
    }

    private IStyleSheetFile createNewStyleSheetFile(String resourceURL) {
        return new StyleSheetFileBuffer(resourceURL);
    }

    protected String filter(IStyleParser cssParser, String styleSheetURL,
            String styleSheetContent, IStyleOperation styleOperations[],
            Map parameters[], IParserContext parserContext) throws IOException {

        if (LOG.isTraceEnabled()) {
            LOG.trace("Process " + styleOperations.length + " style operation"
                    + ((styleOperations.length > 1) ? "s" : "") + " for url='"
                    + styleSheetURL + "'");
        }

        for (int i = 0; i < styleOperations.length; i++) {
            ICssOperation styleOperation = (ICssOperation) styleOperations[i];

            if (LOG.isTraceEnabled()) {
                LOG.trace("Process style operation #" + i + " '"
                        + styleOperation.getName() + "'");
            }

            styleSheetURL = styleOperation.filter(cssParser, styleSheetURL,
                    styleSheetContent, parserContext);
        }

        return styleSheetURL;
    }

    protected String getDefaultCharset() {
        return DEFAULT_CHARSET;
    }

    protected String getDefaultMimeType() {
        return STYLE_CONTENT_TYPE;
    }

    protected boolean isMimeTypeValid(String contentType) {
        return STYLE_CONTENT_TYPE.equalsIgnoreCase(contentType);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:04 $
     */
    protected static class ParserContext implements IParserContext {

        private final FacesContext facesContext;

        private final IResourceVersionHandler resourceVersionHandler;

        private final IResourceLoaderFactory resourceLoaderFactory;

        private final IOperationContentLoader operationContentLoader;

        private final String charset;

        private long lastModifiedDate;

        private boolean processRules;

        private boolean mergeImports;

        public ParserContext(FacesContext facesContext, String charset,
                long lastModifiedDate,
                IResourceVersionHandler resourceVersionHandler,
                IResourceLoaderFactory resourceLoaderFactory,
                IOperationContentLoader operationContentLoader) {
            this.facesContext = facesContext;
            this.charset = charset;
            this.lastModifiedDate = lastModifiedDate;
            this.resourceVersionHandler = resourceVersionHandler;
            this.resourceLoaderFactory = resourceLoaderFactory;
            this.operationContentLoader = operationContentLoader;
        }

        public IResourceLoaderFactory getResourceLoaderFactory() {
            return resourceLoaderFactory;
        }

        public IOperationContentLoader getOperationContentLoader() {
            return operationContentLoader;
        }

        public boolean isProcessRulesEnabled() {
            return processRules;
        }

        public void enableProcessRules() {
            processRules = true;
        }

        public void enableMergeImports() {
            mergeImports = true;
        }

        public boolean isMergeImportsEnabled() {
            return mergeImports;
        }

        public FacesContext getFacesContext() {
            return facesContext;
        }

        public String getCharset() {
            return charset;
        }

        public long getLastModifiedDate() {
            return lastModifiedDate;
        }

        public void setLastModifiedDate(long lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }

        public boolean isVersioningEnabled() {
            return resourceVersionHandler != null;
        }

        public boolean isResourceURLConversionEnabled() {
            return true;
        }

        public IPath processVersioning(IPath base, IPath contextPath,
                IContentFamily contentFamily) {
            if (resourceVersionHandler == null) {
                return contextPath;
            }

            IContentAccessor contentAccessor = new BasicContentAccessor(
                    facesContext, contextPath.toString(),
                    IContentPath.CONTEXT_PATH_TYPE, contentFamily);

            if (IContentFamily.STYLE.equals(contentFamily) == false) {
                // Le style se versionne tout seul !
                contentAccessor = ((ResourceVersionHandlerImpl) resourceVersionHandler)
                        .getVersionedContentAccessor(
                                RcfacesContext.getInstance(facesContext),
                                facesContext, contentAccessor, null);
            }

            String versionedURL = contentAccessor.resolveURL(getFacesContext(),
                    null, null, IContentPath.CONTEXT_PATH_TYPE);
            if (versionedURL.startsWith("/")) {
                versionedURL = versionedURL.substring(1);
            }

            /*
             * if (base.segmentCount() <= 1) { return new Path(versionedURL); }
             */

            // On vient d'une URL versionnée, et on repart sur du versionné
            // La servlet de versioning repostionne l'url à un seul dossier !
            StringAppender sa = new StringAppender("../", 256)
                    .append(versionedURL);

            return new Path(sa.toString());
        }
    }
}