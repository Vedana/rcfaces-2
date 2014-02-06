/*
 * $Id: ExtendedHttpServlet.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.webapp;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.Base64;
import org.rcfaces.core.internal.util.MessageDigestSelector;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public class ExtendedHttpServlet extends HttpServlet {

    private static final long serialVersionUID = -8822434295699331135L;

    private static final Log LOG = LogFactory.getLog(ExtendedHttpServlet.class);

    public static final String HTTP_CONTENT_ENCODING = "Content-Encoding";

    public static final String HTTP_ACCEPT_ENCODING = "Accept-Encoding";

    public static final String HTTP_LAST_MODIFIED = "Last-Modified";

    public static final String HTTP_ETAG = "ETag";

    public static final String HTTP_HASH = "Content-MD5";

    public static final String HTTP_EXPIRES = "Expires";

    public static final String HTTP_CACHE_CONTROL = "Cache-Control";

    public static final String HTTP_IF_MODIFIED_SINCE = "If-Modified-Since";

    public static final String HTTP_IF_NONE_MATCH = "If-None-Match";

    public static final String HTTP_IF_NOT_HASH = "If-Not-Hash";

    public static final String HTTP_VARY = "Vary";

    public static final String HTTP_PRAGMA = "Pragma";

    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    public static final String USER_AGENT = "User-Agent";

    public static final String GZIP_CONTENT_ENCODING = "gzip";

    private static final String DISABLED_GZIP_CONTENT_ENCODING = GZIP_CONTENT_ENCODING
            + ";q=0";

    public static final String HEAD_HTTP_METHOD = "HEAD";

    public static final DateFormat HTTP_DATE_FORMATS[] = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US) };

    public static final String CSS_MIME_TYPE = "text/css";

    public static final String HTML_MIME_TYPE = "text/html";

    public static final String JAVASCRIPT_MIME_TYPE = "text/javascript";

    public static final String TEXT_PLAIN_MIME_TYPE = "text/plain";

    private static Set<String> useGZIPExtensions = new HashSet<String>(8);
    static {
        useGZIPExtensions.add(CSS_MIME_TYPE);
        useGZIPExtensions.add(HTML_MIME_TYPE);
        useGZIPExtensions.add(JAVASCRIPT_MIME_TYPE);
        useGZIPExtensions.add(TEXT_PLAIN_MIME_TYPE);
    }

    public static final boolean isMimeTypeSupportGZip(String mimeType) {
        if (mimeType == null || mimeType.length() < 1) {
            return false;
        }

        int idx = mimeType.indexOf(";");
        if (idx >= 0) {
            mimeType = mimeType.substring(0, idx);
        }

        mimeType = mimeType.trim().toLowerCase();

        return useGZIPExtensions.contains(mimeType);
    }

    public static final boolean hasGzipSupport(HttpServletRequest request) {
        return hasGzipSupport(request.getHeader(HTTP_ACCEPT_ENCODING));
    }

    public static boolean hasGzipSupport(FacesContext facesContext) {
        Map<String, String> requestMap = facesContext.getExternalContext()
                .getRequestHeaderMap();
        String contentEncoding = requestMap.get(HTTP_ACCEPT_ENCODING);

        return hasGzipSupport(contentEncoding);
    }

    private static final boolean hasGzipSupport(String encoding) {
        if (encoding == null) {
            return false;
        }

        if (encoding.toLowerCase().indexOf(GZIP_CONTENT_ENCODING) >= 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Detected GZIP content encoding. (encoding="
                        + encoding + ")");
            }

            return true;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("GZIP content encoding not accepted ! (encoding="
                    + encoding + ")");
        }

        return false;
    }

    public static final String computeETag(byte[] buffer) {
        MessageDigest messageDigest = MessageDigestSelector
                .getInstance(Constants.ETAG_DIGEST_ALGORITHMS);

        byte digest[] = messageDigest.digest(buffer);

        StringAppender sb = new StringAppender(digest.length * 2 + 16);
        sb.append("\"rcfaces:");
        for (int i = 0; i < digest.length; i++) {
            int v = digest[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }

        sb.append(':');
        sb.append(Integer.toHexString(buffer.length));

        sb.append('\"');
        return sb.toString();
    }

    public static final String computeHash(byte[] buffer) {
        MessageDigest messageDigest = MessageDigestSelector
                .getInstance(Constants.HASH_DIGEST_ALGORITHMS);

        // messageDigest.update(buffer);
        byte digest[] = messageDigest.digest(buffer);

        return Base64.encodeBytes(digest, Base64.DONT_BREAK_LINES);

    }

    public static final Date parseHttpDate(String date) throws ParseException {

        ParseException ex = null;
        for (int i = 0; i < HTTP_DATE_FORMATS.length; i++) {
            try {
                Date d = HTTP_DATE_FORMATS[i].parse(date);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("ParseHttpDate returns '" + d + "' for string '"
                            + date + "'.");
                }

                return d;

            } catch (ParseException e) {
                ex = e;
            }
        }

        if (ex == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ParseHttpDate has not recognized date '" + date
                        + "'.");
            }

            return null;
        }

        LOG.error("ParseHttpDate has not recognized date '" + date + "'.", ex);

        throw ex;
    }

    protected final String getParameter(String name) {
        return getParameter(getServletConfig(), name);
    }

    public static final String getParameter(ServletConfig config, String name) {
        String initParameter = config.getInitParameter(name);
        if (initParameter != null) {
            return initParameter;
        }

        initParameter = config.getServletContext().getInitParameter(name);
        return initParameter;
    }

  
    public static final void setNoCache(HttpServletResponse response) {
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader(HTTP_PRAGMA, "no-cache");

        // Set standard HTTP/1.1 no-cache headers.

        response.setHeader(HTTP_CACHE_CONTROL,
                "no-cache, no-store, must-revalidate");

        // Set IE extended HTTP/1.1 no-cache headers (use addHeader)
        response.addHeader(HTTP_CACHE_CONTROL, "post-check=0, pre-check=0");

        response.setDateHeader(HTTP_EXPIRES, 0);
        response.setDateHeader(HTTP_LAST_MODIFIED, System.currentTimeMillis());
    }

    public static void setVaryAcceptEncoding(HttpServletResponse response) {
        response.setHeader(HTTP_VARY, ACCEPT_ENCODING);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set Vary accept encoding.");
        }
    }

    public static void setVaryUserAgent(HttpServletResponse response) {
        response.setHeader(HTTP_VARY, USER_AGENT);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set Vary user agent.");
        }
    }

    public static void setGzipContentEncoding(HttpServletResponse response,
            boolean declareVaryAcceptEncoding) {
        response.setHeader(HTTP_CONTENT_ENCODING, GZIP_CONTENT_ENCODING);
        if (declareVaryAcceptEncoding) {
            response.setHeader(HTTP_VARY, ACCEPT_ENCODING);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set GZIP content encoding.");
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Process http request requestURI='"
                        + req.getRequestURI() + "'.");
            }

            super.service(req, resp);

            if (LOG.isTraceEnabled()) {
                LOG.trace("Http request processed requestURI='"
                        + req.getRequestURI() + "'. (response contentType="
                        + resp.getContentType() + ")");
            }

        } catch (RuntimeException ex) {
            LOG.error("Exception request=" + req.getRequestURI(), ex);

        } catch (Error ex) {
            LOG.error("Error request=" + req.getRequestURI(), ex);
        }
    }

}
