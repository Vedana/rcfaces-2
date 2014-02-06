/*
 * $Id: ContentStorageServlet.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.util.ServletTools;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.internal.webapp.ExpirationDate;
import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class ContentStorageServlet extends ConfiguredHttpServlet {

    private static final long serialVersionUID = 2315096075392789304L;

    private static final Log LOG = LogFactory
            .getLog(ContentStorageServlet.class);

    private static final String DEFAULT_CONTENT_STORAGE_BASE_URL = "/ap-content";

    private static final String CONTENT_STORAGE_URL_PROPERTY = "org.rcfaces.core.internal.contentStorage.CONTENT_STORAGE_URL";

    private static final String NO_CACHE_PARAMETER = Constants
            .getPackagePrefix() + ".NO_CACHE";

    private static final int MAX_BUFFER_SIZE = 8192;

    private IContentStorageRepository contentStorageRepository;

    private boolean noCache;

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        String nc = getParameter(NO_CACHE_PARAMETER);
        if ("true".equalsIgnoreCase(nc)) {
            noCache = true;
            LOG.info("Enable 'no cache' mode.");
        }

        String contentStorageBaseURL = ServletTools.computeResourceURI(
                getServletContext(), DEFAULT_CONTENT_STORAGE_BASE_URL,
                getClass());
        if (contentStorageBaseURL == null) {
            LOG.info("Base of content storage is invalid, ignore content service.");
            return;
        }

        LOG.debug("Base of content storage is '" + contentStorageBaseURL + "'.");

        getServletContext().setAttribute(CONTENT_STORAGE_URL_PROPERTY,
                contentStorageBaseURL);
    }

    public static String getContentStorageBaseURI(
            Map<String, Object> applicationMap) {
        return (String) applicationMap.get(CONTENT_STORAGE_URL_PROPERTY);
    }

    @Override
    protected void doHead(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {

        String url = request.getRequestURI();

        String contextPath = request.getContextPath();
        if (contextPath != null) {
            url = url.substring(contextPath.length());
        }

        String servletPath = request.getServletPath();
        if (servletPath != null) {
            url = url.substring(servletPath.length());
        }

        // Retire le nom de notre servlet
        int idx = url.indexOf('/');
        if (idx < 0) {
            throw new ServletException("Can not understand URI '"
                    + request.getRequestURI() + "'.");
        }

        url = url.substring(idx + 1);

        synchronized (this) {
            if (contentStorageRepository == null) {
                RcfacesContext rcfacesContext = RcfacesContext.getInstance(
                        getServletContext(), request, response);

                contentStorageRepository = rcfacesContext
                        .getContentStorageEngine().getRepository();
            }
        }

        final String contentKey = url;

        final IResolvedContent resolvedContent = contentStorageRepository
                .load(contentKey);

        if (resolvedContent == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("doGet: contentKey '" + contentKey + "' => "
                        + resolvedContent);
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final boolean processAtRequest = resolvedContent.isProcessAtRequest();

        if (LOG.isDebugEnabled()) {
            LOG.debug("doGet: contentKey '" + contentKey + "' => '"
                    + resolvedContent + "' process at request '"
                    + processAtRequest + "'.");
        }

        final IOException exceptionRef[] = new IOException[1];
        RcfacesContext.runIntoFacesContext(getServletContext(), request,
                response, new Runnable() {

                    public void run() {
                        try {
                            sendContent(request, response, resolvedContent);

                            if (processAtRequest == false) {
                                return;
                            }

                            if (resolvedContent instanceof IResolvedContentWrapper) {
                                IResolvedContent wrapped = ((IResolvedContentWrapper) resolvedContent)
                                        .getResolvedContent();

                                contentStorageRepository.saveWrapped(
                                        contentKey, wrapped);
                            }
                        } catch (IOException e) {
                            exceptionRef[0] = e;
                        }
                    }

                });

        if (exceptionRef[0] != null) {
            throw exceptionRef[0];
        }
    }

    protected void sendContent(HttpServletRequest request,
            HttpServletResponse response, IResolvedContent resolvedContent)
            throws IOException {
        if (resolvedContent.isErrored()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (resolvedContent instanceof IResolvedContentWrapper) {
            resolvedContent = ((IResolvedContentWrapper) resolvedContent)
                    .getResolvedContent();
        }

        long modificationDate = resolvedContent.getModificationDate();
        if (modificationDate > 0) {
            modificationDate -= (modificationDate % 1000);
        }

        boolean isVersioned = resolvedContent.isVersioned();

        if (noCache) {
            setNoCache(response);

        } else {
            if (modificationDate > 0) {
                response.setDateHeader(HTTP_LAST_MODIFIED, modificationDate);
            }

            ExpirationDate expirationDate = getDefaultExpirationDate(isVersioned);

            if (expirationDate != null) {
                expirationDate.sendExpires(response);
            }
        }

        boolean different = noCache;

        String etag = resolvedContent.getETag();
        if (different == false && etag != null) {
            String ifETag = request.getHeader(HTTP_IF_NONE_MATCH);
            if (ifETag != null) {
                if (etag.equals(ifETag)) {

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Client sent the same ETag, send a NOT MODIFIED response.");
                    }

                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }

                different = true;
            }
        }

        if (resolvedContent instanceof IGzipedResolvedContent) {

            setVaryAcceptEncoding(response);

            if (hasGzipSupport(request)) {
                IResolvedContent gzipedResolvedContent = ((IGzipedResolvedContent) resolvedContent)
                        .getGzipedContent();

                if (gzipedResolvedContent != null) {
                    resolvedContent = gzipedResolvedContent;

                    if (ExtendedHttpServlet.GZIP_CONTENT_ENCODING
                            .equals(resolvedContent.getContentEncoding())) {
                        // On encode pas forcement en GZIP (taille<256 octets)

                        setGzipContentEncoding(response, false);
                    }
                }
            }
        }

        String hash = resolvedContent.getHash();
        if (different == false && hash != null) {
            String isHash = request.getHeader(HTTP_IF_NOT_HASH);
            if (isHash != null) {
                if (hash.equals(isHash)) {

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Client sent the same HashCode, send a NOT MODIFIED response.");
                    }

                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }

                different = true;
            }
        }

        if (different == false && modificationDate > 0) {
            long ifModifiedSince = request
                    .getDateHeader(HTTP_IF_MODIFIED_SINCE);
            if (ifModifiedSince > 0) {
                ifModifiedSince -= (ifModifiedSince % 1000);

                if (ifModifiedSince >= modificationDate) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Client sent a valid date for last modification, send a NOT MODIFIED response.");
                    }

                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }
        }

        String contentType = resolvedContent.getContentType();
        response.setContentType(contentType);

        int length = resolvedContent.getLength();
        if (length > 0) {
            response.setContentLength(length);
        }

        long lastModified = resolvedContent.getModificationDate();
        if (lastModified > 0) {
            response.setDateHeader(HTTP_LAST_MODIFIED, lastModified);
        }

        if (etag != null) {
            response.setHeader(HTTP_ETAG, etag);
        }

        if (hash != null) {
            response.setHeader(HTTP_HASH, hash);
        }

        if (request.getMethod().equals(HEAD_HTTP_METHOD)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Head method requested, dont send the data.");
            }
            return;
        }

        int bufferSize = response.getBufferSize();
        if (bufferSize < 1 || bufferSize > MAX_BUFFER_SIZE) {
            bufferSize = MAX_BUFFER_SIZE;
        }

        byte buffer[] = new byte[bufferSize];

        InputStream inputStream = null;

        /*
         * if (hasGZipSupport() && hasGzipSupport(request)) { if (bufferSize >
         * 128) { inputStream = new GZIPInputStream(inputStream, bufferSize); }
         * }
         */

        if (inputStream == null) {
            inputStream = resolvedContent.getInputStream();
        }

        try {
            OutputStream outputStream = response.getOutputStream();

            for (;;) {
                int ret = inputStream.read(buffer);
                if (ret <= 0) {
                    break;
                }

                outputStream.write(buffer, 0, ret);
            }

            outputStream.close();

        } finally {
            inputStream.close();
        }
    }

}
