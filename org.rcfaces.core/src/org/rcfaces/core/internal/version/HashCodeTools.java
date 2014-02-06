/*
 * $Id: HashCodeTools.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.version;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.Base64;
import org.rcfaces.core.internal.util.IncludeHttpServletRequest;
import org.rcfaces.core.internal.util.IncludeHttpServletResponse;
import org.rcfaces.core.internal.util.MessageDigestSelector;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class HashCodeTools {

    private static final Log LOG = LogFactory.getLog(HashCodeTools.class);

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int INITIAL_BUFFER_SIZE = 8000;

    public static final String computeURLFormat(FacesContext facesContext,
            String url, Object content, int maxHashCodeSize) {

        String hash = compute(facesContext, url, content, maxHashCodeSize);
        if (hash == null) {
            return null;
        }

        return hash.replace('+', '.').replace('/', ':');
    }

    public static final String compute(FacesContext facesContext, String url,
            Object content, int maxHashCodeSize) {

        byte buffer[] = null;
        if (content instanceof URL) {
            buffer = loadContentFromURL((URL) content);

        } else if (content instanceof String) {
            try {
                buffer = ((String) content).getBytes("UTF-8");

            } catch (UnsupportedEncodingException e) {
                throw new FacesException(e);
            }

        } else if (content instanceof StringAppender) {
            try {
                buffer = ((StringAppender) content).getBytes("UTF-8");

            } catch (UnsupportedEncodingException e) {
                throw new FacesException(e);
            }

        } else if (content instanceof byte[]) {
            buffer = (byte[]) content;

        } else if (content instanceof InputStream) {
            ByteBufferOutputStream bos = new ByteBufferOutputStream(
                    INITIAL_BUFFER_SIZE);

            InputStream ins = (InputStream) content;

            try {
                byte buf[] = new byte[INITIAL_BUFFER_SIZE];
                for (;;) {
                    int ret = ins.read(buf);
                    if (ret < 1) {
                        break;
                    }

                    bos.write(buf, 0, ret);
                }

            } catch (IOException ex) {
                LOG.error("Can not read content of stream.", ex);
                return null;
            }

            bos.close();

            buffer = bos.toByteArray();

        } else if (content == null) {
            buffer = loadContentFromPath(facesContext, url);
        }

        if (buffer == null) {
            LOG.error("Can not get content of buffer url='" + url
                    + "' content='" + content + "'.");
            return null;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Compute HashCode of buffer (size="
                    + buffer.length
                    + ") using '"
                    + Arrays.asList(Constants.RESOURCE_VERSION_DIGEST_ALGORITHMS)
                    + " algorithm (maxHashCodeSize=" + maxHashCodeSize + ")");
        }

        MessageDigest messageDigest = MessageDigestSelector
                .getInstance(Constants.RESOURCE_VERSION_DIGEST_ALGORITHMS);

        byte digest[] = messageDigest.digest(buffer);

        String etag = Base64.encodeBytes(digest, Base64.DONT_BREAK_LINES);
        if (etag.endsWith("==")) { // Ca fait pas joli !
            etag = etag.substring(0, etag.length() - 2);
        }

        if (maxHashCodeSize == 0) {
            maxHashCodeSize = Constants.VERSIONED_URI_HASHCODE_MAX_SIZE;
        }
        if (maxHashCodeSize > 0 && etag.length() > maxHashCodeSize) {
            etag = etag.substring(0, maxHashCodeSize);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Compute ETag from url '" + url + "' = '" + etag + "'.");
        }

        etag += buffer.length;

        return etag;
    }

    private static byte[] loadContentFromURL(URL content) {
        URLConnection connection;
        try {
            connection = content.openConnection();
        } catch (IOException ex) {
            LOG.error("Can not open connection of '" + content + "'.", ex);
            return null;
        }

        int length = connection.getContentLength();
        if (length < 1) {
            length = 8000;
        }

        InputStream ins;
        try {
            ins = connection.getInputStream();

        } catch (IOException ex) {
            LOG.error("Can not get input stream of '" + content + "'.", ex);
            return null;
        }
        try {
            ByteBufferOutputStream out = new ByteBufferOutputStream(length);
            byte buf[] = new byte[INITIAL_BUFFER_SIZE];
            for (;;) {
                int ret = ins.read(buf);
                if (ret < 1) {
                    break;
                }

                out.write(buf, 0, ret);
            }

            byte b[] = out.toByteArray();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Read content of url '" + content + "' (" + b.length
                        + " bytes).");
            }

            return b;

        } catch (IOException ex) {
            LOG.error("Can not get read content of '" + content + "'.", ex);
            return null;

        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    private static byte[] loadContentFromPath(FacesContext facesContext,
            String url) {

        ExternalContext externalContext = facesContext.getExternalContext();

        ServletContext servletContext = (ServletContext) externalContext
                .getContext();
        HttpServletRequest servletRequest = (HttpServletRequest) externalContext
                .getRequest();
        HttpServletResponse servletResponse = (HttpServletResponse) externalContext
                .getResponse();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Compute ETag from url '" + url + "'.");
        }

        if (url.startsWith("/") == false) {
            url = "/" + url;
        }

        RequestDispatcher requestDispatcher = servletContext
                .getRequestDispatcher(url);
        if (requestDispatcher == null) {
            LOG.error("Can not find dispatcher for url '" + url + "'.");

            return null;
        }

        ByteBufferOutputStream outputStream = new ByteBufferOutputStream(
                INITIAL_BUFFER_SIZE);

        ServletRequest request = new IncludeHttpServletRequest(servletRequest);
        IncludeHttpServletResponse response = IncludeHttpServletResponse
                .create(servletResponse, outputStream, DEFAULT_CHARSET);

        try {
            requestDispatcher.include(request, response);

        } catch (Throwable e) {
            LOG.error("Can not include url '" + url
                    + "' to compute ETag value.", e);

            return null;
        }

        if (response.getStatus() != HttpServletResponse.SC_OK) {
            // Erreur !
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invalid response status for url '" + url + "'.");
            }

            return null;
        }

        return outputStream.toByteArray();
    }
}
