package org.rcfaces.core.internal.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.IResourceKeyParticipant;
import org.rcfaces.core.internal.contentStorage.IResolvedContent;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory.IResourceLoader;
import org.rcfaces.core.internal.version.HashCodeTools;
import org.rcfaces.core.lang.IAdaptable;
import org.rcfaces.core.model.BasicContentModel;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public abstract class AbstractOperationContentModel extends BasicContentModel
        implements Serializable, IResolvedContent, IAdaptable {

    private static final long serialVersionUID = 4218209439999498360L;

    private static final Log LOG = LogFactory
            .getLog(AbstractOperationContentModel.class);

    private static final int RESOURCE_KEY_INITIAL_SIZE = 4096;

    private static final FileNameMap fileNameMap = URLConnection
            .getFileNameMap();

    /**
     * 
     */
    protected static final IFileBuffer INVALID_BUFFERED_FILE = new IFileBuffer() {

        public int getSize() {
            return 0;
        }

        public String getName() {
            return "*** Invalid buffer ***";
        }

        public InputStream getContent() {
            return null;
        }

        public String getContentType() {
            return null;
        }

        public long getModificationDate() {
            return 0;
        }

        public String getHash() {
            return null;
        }

        public String getETag() {
            return null;
        }

        public boolean isInitialized() {
            return true;
        }

        public void setErrored() {
        }

        public boolean isErrored() {
            return true;
        }

        public String getRedirection() {
            return null;
        }

        public void initializeRedirection(String url) {
        }

    };

    private static final int MAX_URL_HASH_LENGTH = 32;

    private final String resourceURL;

    private final String operationId;

    private String filterParametersToParse;

    private String resourceKey;

    private transient Map<String, Object> filterParameters;

    private transient IBufferOperation bufferOperation;

    private boolean versioned;

    private transient IFileBuffer fileBuffer;

    private boolean sourceInfoRecorded = false;

    private long sourceLastModified;

    private long sourceLength;

    private final String versionId;

    private final String specifiedResourceKey;

    public AbstractOperationContentModel(String resourceURL, String versionId,
            String operationId, String filterParametersToParse,
            IBufferOperation bufferOperation, String specifiedResourceKey) {
        this.resourceURL = resourceURL;
        this.operationId = operationId;
        this.filterParametersToParse = filterParametersToParse;
        this.bufferOperation = bufferOperation;
        this.versionId = versionId;
        this.specifiedResourceKey = specifiedResourceKey;

        setWrappedData(this);
    }

    @Override
    public void setInformations(
            IGenerationResourceInformation generationInformation,
            IGeneratedResourceInformation generatedInformation) {
        super.setInformations(generationInformation, generatedInformation);

        String contentType = generatedInformation.getResponseMimeType();
        if (contentType == null) {
            contentType = generatedInformation.getSourceMimeType();
        }

        String suffix = generatedInformation.getResponseSuffix();

        if (suffix == null && contentType != null) {
            suffix = ContentAdapterFactory.getSuffixByMimeType(contentType);

            generatedInformation.setResponseSuffix(suffix);
        }
    }

    public final synchronized Map<String, Object> getFilterParameters() {
        if (filterParameters != null) {
            return filterParameters;
        }

        if (filterParametersToParse == null
                || filterParametersToParse.length() < 1) {
            filterParameters = Collections.emptyMap();
            return filterParameters;
        }

        StringTokenizer st = new StringTokenizer(filterParametersToParse, ",");

        filterParameters = new HashMap<String, Object>(st.countTokens());
        int idx = 0;
        for (; st.hasMoreTokens();) {
            String token = st.nextToken().trim();

            String pName;
            String pValue;

            int idxEq = token.indexOf('=');
            if (idxEq >= 0) {
                pName = token.substring(0, idxEq).trim();
                pValue = token.substring(idxEq + 1).trim();

            } else {
                pName = "#" + idx;
                pValue = token.trim();

                idx++;
            }

            filterParameters.put(pName, pValue);
        }

        return filterParameters;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> adapter, Object parameter) {
        if (IResolvedContent.class.equals(adapter)) {
            return (T) getResolvedContent();
        }

        return null;
    }

    protected IResolvedContent getResolvedContent() {
        return this;
    }

    public boolean isProcessAtRequest() {
        return false;
    }

    public boolean isErrored() {
        return getFileBuffer().isErrored();
    }

    private synchronized IFileBuffer getFileBuffer() {
        if (fileBuffer != null) {
            return fileBuffer;
        }

        fileBuffer = createFileBuffer();

        generatedInformation.setResponseMimeType(fileBuffer.getContentType());

        return fileBuffer;
    }

    public InputStream getInputStream() throws IOException {
        return getFileBuffer().getContent();
    }

    public IBufferOperation getBufferOperation(FacesContext facesContext) {
        if (bufferOperation != null) {
            return bufferOperation;
        }

        bufferOperation = createBufferOperation(facesContext);

        return bufferOperation;
    }

    protected abstract IBufferOperation createBufferOperation(
            FacesContext facesContext);

    protected abstract IFileBuffer createFileBuffer();

    protected final String getResourceURL() {
        return resourceURL;
    }

    protected final String getOperationId() {
        return operationId;
    }

    public long getModificationDate() {
        return getFileBuffer().getModificationDate();
    }

    public int getLength() {
        return getFileBuffer().getSize();
    }

    public String getETag() {
        return getFileBuffer().getETag();
    }

    public String getHash() {
        return getFileBuffer().getHash();
    }

    public String getContentEncoding() {
        return null;
    }

    public String getResourceKey() {
        synchronized (this) {
            if (resourceKey == null) {
                StringAppender sa = new StringAppender(operationId,
                        RESOURCE_KEY_INITIAL_SIZE);

                if (filterParametersToParse != null) {
                    sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR);
                    sa.append(filterParametersToParse);
                }

                sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR);

                sa.append(resourceURL);

                if (versionId != null) {
                    sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR);
                    sa.append(versionId);
                }

                if (generationInformation instanceof IResourceKeyParticipant) {
                    ((IResourceKeyParticipant) generationInformation)
                            .participeKey(sa);
                }

                if (generatedInformation instanceof IResourceKeyParticipant) {
                    ((IResourceKeyParticipant) generatedInformation)
                            .participeKey(sa);
                }

                resourceKey = sa.toString();
            }

        }
        return resourceKey;
    }

    public boolean isVersioned() {
        return versioned;
    }

    public void setVersioned(boolean versioned) {
        this.versioned = versioned;
    }

    public void appendHashInformations(StringAppender sa) {
        String url = getResourceURL();
        if (url != null) {
            loadSourceInfos(null);
        }

        StringAppender sa2 = new StringAppender(512);

        if (url != null) {
            sa2.append(url);
        }

        if (sourceLastModified > 0) {
            sa2.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR);
            sa2.append(sourceLastModified);
        }

        if (sourceLength > 0) {
            sa2.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR);
            sa2.append(sourceLength);
        }

        String key = sa2.toString();

        String result = HashCodeTools.computeURLFormat(null, key, key,
                MAX_URL_HASH_LENGTH);

        sa.append(result);
    }

    private synchronized void loadSourceInfos(FacesContext facesContext) {
        if (sourceInfoRecorded) {
            return;
        }
        sourceInfoRecorded = true;

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        IResourceLoaderFactory resourceLoaderFactory = getResourceLoaderFactory(facesContext);

        ExternalContext externalContext = facesContext.getExternalContext();

        HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        HttpServletResponse response = (HttpServletResponse) externalContext
                .getResponse();
        ServletContext context = (ServletContext) externalContext.getContext();

        IResourceLoader resourceLoader = resourceLoaderFactory.loadResource(
                context, request, response, getResourceURL());

        sourceLastModified = resourceLoader.getLastModified();
        sourceLength = resourceLoader.getContentLength();
    }

    protected abstract IResourceLoaderFactory getResourceLoaderFactory(
            FacesContext facesContext);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((filterParametersToParse == null) ? 0
                        : filterParametersToParse.hashCode());
        result = prime * result
                + ((operationId == null) ? 0 : operationId.hashCode());
        result = prime * result
                + ((resourceURL == null) ? 0 : resourceURL.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractOperationContentModel other = (AbstractOperationContentModel) obj;
        if (filterParametersToParse == null) {
            if (other.filterParametersToParse != null)
                return false;
        } else if (!filterParametersToParse
                .equals(other.filterParametersToParse))
            return false;
        if (operationId == null) {
            if (other.operationId != null)
                return false;
        } else if (!operationId.equals(other.operationId))
            return false;
        if (resourceURL == null) {
            if (other.resourceURL != null)
                return false;
        } else if (!resourceURL.equals(other.resourceURL))
            return false;
        return true;
    }

    public String getContentType() {
        return generatedInformation.getResponseMimeType();
    }

    public String getURLSuffix() {
        return generatedInformation.getResponseSuffix();
    }

}