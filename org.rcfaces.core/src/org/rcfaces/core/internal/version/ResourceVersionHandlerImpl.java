/*
 * $Id: ResourceVersionHandlerImpl.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.version;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.BasicContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IContentVersionHandler;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class ResourceVersionHandlerImpl extends AbstractProvider implements
        IResourceVersionHandler, IContentVersionHandler {

    private static final Log LOG = LogFactory
            .getLog(ResourceVersionHandlerImpl.class);

    private static final String APPLICATION_VERSION_URI_PARAMETER = Constants
            .getPackagePrefix() + ".APPLICATION_VERSION";

    private static final String HASHCODE_KEYWORD = "hashcode";

    private static final String NOW_KEYWORD = "now";

    private static final int HASHCODE_CACHE_INITIAL_SIZE = 128;

    private String prefixURI;

    private String version;

    private Map<String, ResourceHashCode> hashCodeByURL = null;

    public ResourceVersionHandlerImpl() {
    }

    public String getId() {
        return "ResourceVersionHandler";
    }

    @Override
    public void startup(FacesContext facesContext) {

        ExternalContext externalContext = facesContext.getExternalContext();

        String applicationVersionURI = ApplicationVersionServlet
                .getApplicationVersionURI(externalContext.getApplicationMap());
        if (applicationVersionURI == null) {
            LOG.debug("Can not find application version pattern ! (Servlet can be not defined)");
            this.prefixURI = null;
            this.version = null;
            return;
        }

        String version = externalContext
                .getInitParameter(APPLICATION_VERSION_URI_PARAMETER);
        if (version == null || "none".equalsIgnoreCase(version)) {
            this.prefixURI = null;
            this.version = null;
            LOG.info("Disable application version rewriting engine. (context parameter '"
                    + APPLICATION_VERSION_URI_PARAMETER + "=" + version + "')");
            return;
        }

        if (HASHCODE_KEYWORD.equalsIgnoreCase(version)) {
            version = HASHCODE_KEYWORD;

            prefixURI = applicationVersionURI;
            hashCodeByURL = new HashMap<String, ResourceHashCode>(
                    HASHCODE_CACHE_INITIAL_SIZE);

            LOG.info("Use resource hashcode as the resource version.");

        } else {
            if (NOW_KEYWORD.equalsIgnoreCase(version)) {
                version = "0." + System.currentTimeMillis();

                LOG.info("Set resource version to a random value: '" + version
                        + "'.");

            } else {
                LOG.info("Set resource version to specified value: '" + version
                        + "'.");
            }

            prefixURI = applicationVersionURI + "/" + version;
            LOG.info("Set resource version url to '" + prefixURI + "'.");
        }

        this.version = version;

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);
        if (rcfacesContext.getResourceVersionHandler() == null) {
            rcfacesContext.setResourceVersionHandler(this);
        }

        if (rcfacesContext.getDefaultContentVersionHandler() == null) {
            rcfacesContext.setDefaultContentVersionHandler(this);
        }
    }

    public boolean isEnabled() {
        return prefixURI != null;
    }

    public String getVersionTag(RcfacesContext rcfacesContext,
            FacesContext facesContext, String relativeUrl,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation contentInformation) {

        if (prefixURI == null) {
            return null;
        }

        IProcessContext processContext = AbstractProcessContext
                .getProcessContext(facesContext);

        String absoluteURLWithoutPrefix = processContext.getAbsolutePath(
                relativeUrl, false);

        return getResourceVersion(facesContext, absoluteURLWithoutPrefix, null);
    }

    public IContentAccessor getVersionedContentAccessor(
            RcfacesContext rcfacesContext, FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformation) {

        if (prefixURI == null) {
            return null;
        }

        String url = (String) contentAccessor.getContentRef();
        int pathType = contentAccessor.getPathType();
        switch (pathType) {

        case IContentPath.RELATIVE_PATH_TYPE:
            IProcessContext processContext = AbstractProcessContext
                    .getProcessContext(facesContext);

            url = processContext.getAbsolutePath(url, false);
            break;

        case IContentPath.CONTEXT_PATH_TYPE:
            break;

        default:
            return null;
        }

        if (hashCodeByURL == null) {
            // Le prefixURI contient la version !
            StringAppender sa = new StringAppender(prefixURI, 1
                    + version.length() + url.length());
            sa.append('/');
            sa.append(version);
            sa.append(url);

            return new BasicContentAccessor(facesContext, sa.toString(),
                    contentAccessor, IContentPath.CONTEXT_PATH_TYPE);
        }

        StringAppender sa = new StringAppender(prefixURI, 1
                + Constants.VERSIONED_URI_HASHCODE_MAX_SIZE + url.length() + 4); // 4
        // =
        // sécurité
        // !

        String etag = getResourceVersion(facesContext, url, null);

        sa.append('/');
        sa.append(etag);

        if (url.length() > 0 && url.charAt(0) != '/') {
            sa.append('/');
        }

        sa.append(url);

        return new BasicContentAccessor(facesContext, sa.toString(),
                contentAccessor, IContentPath.CONTEXT_PATH_TYPE);
    }

    public String getResourceVersion(FacesContext facesContext,
            String absolutePath, URL url) {
        if (hashCodeByURL == null) {
            return version;
        }

        ResourceHashCode tag;
        synchronized (hashCodeByURL) {
            tag = hashCodeByURL.get(absolutePath);
            if (tag == null) {
                tag = new ResourceHashCode(absolutePath, url);
                hashCodeByURL.put(absolutePath, tag);
            }
        }

        return tag.getHashCode(facesContext);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    private static final class ResourceHashCode {

        private static final String NULL_HASH_CODE = "ERR";

        private final String absolutePath;

        private final URL url;

        private String etag;

        public ResourceHashCode(String absolutePath, URL url) {
            this.absolutePath = absolutePath;
            this.url = url;
        }

        public synchronized String getHashCode(FacesContext facesContext) {
            if (etag == null) {
                etag = HashCodeTools.computeURLFormat(facesContext,
                        absolutePath, url,
                        Constants.VERSIONED_URI_HASHCODE_MAX_SIZE);
                if (etag == null) {
                    etag = NULL_HASH_CODE;
                }
            }
            return etag;
        }

    }
}
