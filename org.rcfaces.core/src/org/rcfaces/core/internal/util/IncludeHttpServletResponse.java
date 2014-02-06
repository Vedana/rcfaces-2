package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/07/03 12:25:05 $
 */
public class IncludeHttpServletResponse extends HttpServletResponseWrapper {

    private static final Log LOG = LogFactory
            .getLog(IncludeHttpServletResponse.class);

    private final ServletOutputStream servletOutputStream;

    private final PrintWriter writer;

    private int status = HttpServletResponse.SC_OK;

    private int contentLength;

    private String contentType;

    private long lastModified;

    private IncludeHttpServletResponse(HttpServletResponse servletResponse,
            final OutputStream outputStream, Writer writer) {
        super(servletResponse);

        this.writer = (writer != null) ? new PrintWriter(writer) : null;

        servletOutputStream = new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public void flush() throws IOException {
                outputStream.flush();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                outputStream.write(b, off, len);
            }

            @Override
            public void write(byte[] b) throws IOException {
                outputStream.write(b);
            }

        };
    }

    public static IncludeHttpServletResponse create(
            HttpServletResponse servletResponse, OutputStream output,
            String defaultCharset) {

        String charset = servletResponse.getCharacterEncoding();
        if (charset == null) {
            charset = defaultCharset;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Use charset '" + charset + "' for writer.");
        }

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(output, charset);

        } catch (UnsupportedEncodingException e) {
            LOG.error("Unsupported encoding '" + charset + "'.", e);
        }

        return new IncludeHttpServletResponse(servletResponse, output, writer);
    }

    public int getContentLength() {
        return contentLength;
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            throw new IOException("No writer supported !");
        }
        return writer;
    }

    @Override
    public void addCookie(Cookie arg0) {
    }

    @Override
    public void addDateHeader(String name, long date) {
        setDateHeader(name, date);
    }

    @Override
    public void addHeader(String arg0, String arg1) {
        setHeader(arg0, arg1);
    }

    @Override
    public void addIntHeader(String arg0, int arg1) {
        setIntHeader(arg0, arg1);
    }

    @Override
    public void sendRedirect(String arg0) {
    }

    @Override
    public void setDateHeader(String name, long date) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Inclusion set date header '" + name + "' to '" + date
                    + "'.");
        }

        if (this.lastModified == 0
                && ExtendedHttpServlet.HTTP_LAST_MODIFIED.equals(name)) {

            this.lastModified = date;
        }
    }

    @Override
    public void setHeader(String name, String value) {
        if (LOG.isTraceEnabled()) {
            LOG
                    .trace("Inclusion set header '" + name + "' to '" + value
                            + "'.");
        }

        if (lastModified == 0
                && ExtendedHttpServlet.HTTP_LAST_MODIFIED.equals(name)) {

            if (value == null || value.length() < 1) {
                lastModified = 0;
                return;
            }

            try {
                Date d = ExtendedHttpServlet.parseHttpDate(value);
                LOG.trace("Inclusion set Last-Modified property to " + d);

                if (d != null) {
                    lastModified = d.getTime();
                }

            } catch (ParseException ex) {
                LOG.error("Can not parse http date '" + value + "'.", ex);
            }
        }
    }

    @Override
    public void setIntHeader(String arg0, int arg1) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Inclusion set int header '" + arg0 + "' to '" + arg1
                    + "'.");
        }
    }

    @Override
    public void flushBuffer() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public void setBufferSize(int arg0) {
    }

    @Override
    public void setCharacterEncoding(String arg0) {
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setLocale(Locale arg0) {
    }

    @Override
    public void sendError(int status) throws IOException {
        super.sendError(status);

        this.status = status;
    }

    @Override
    public void setStatus(int status) {
        super.setStatus(status);

        this.status = status;
    }

    @Override
    public void sendError(int status, String arg1) throws IOException {
        super.sendError(status, arg1);

        this.status = status;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setStatus(int status, String arg1) {
        super.setStatus(status, arg1);

        this.status = status;
    }
}