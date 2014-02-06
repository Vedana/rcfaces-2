/*
 * $Id: RepositoryServlet.java,v 1.4 2013/11/13 12:53:24 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.faces.FacesException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.repository.IRepository.IContent;
import org.rcfaces.core.internal.repository.IRepository.IContentProvider;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.internal.webapp.ExpirationDate;
import org.rcfaces.core.internal.webapp.URIParameters;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
 */
public abstract class RepositoryServlet extends ConfiguredHttpServlet {

    private static final long serialVersionUID = 7028775289298926045L;

    private static final Log LOG = LogFactory.getLog(RepositoryServlet.class);

    private static final byte[] BYTE_EMPTY_ARRAY = new byte[0];

    private static final String SET_PREFIX = ".sets";

    private static final String MODULES_PREFIX = ".modules";

    private static final String NO_CACHE_PARAMETER = Constants
            .getPackagePrefix() + ".NO_CACHE";

    private static final String GROUP_ALL_DEFAULT_VALUE = null;

    private static final String BOOT_SET_DEFAULT_VALUE = null;

    private static final int CONTENT_INITIAL_SIZE = 16000;

    private final Map<ICriteria, Map<IFile, Record>> fileToRecordByCriteria = new HashMap<ICriteria, Map<IFile, Record>>(
            128);

    private IRepository repository;

    private boolean noCache;

    private boolean devMode;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String nc = getParameter(getNoCacheParameterName());
        if ("true".equalsIgnoreCase(nc)) {
            noCache = true;

            LOG.info("Enable NO_CACHE for servlet '" + getServletName() + "'.");
        }

        String repositoryDevModePropertyName = getRepositoryDevModeParameterName();
        if (repositoryDevModePropertyName != null) {
            String dev = getParameter(repositoryDevModePropertyName);

            if ("true".equalsIgnoreCase(dev)) {
                devMode = true;

                LOG.info("Enable REPOSITORY_DEV_MODE for servlet '"
                        + getServletName() + "'.");
            }
        }

        try {
            repository = initializeRepository(config);

        } catch (IOException e) {
            throw new ServletException(
                    "Can not initialize repository for servlet '"
                            + getServletName() + "'.", e);
        }
    }

    protected String getNoCacheParameterName() {
        return NO_CACHE_PARAMETER;
    }

    protected String getRepositoryDevModeParameterName() {
        return null;
    }

    protected abstract String getParameterPrefix();

    protected final IRepository getRepository() {
        return repository;
    }

    protected abstract IRepository initializeRepository(ServletConfig config)
            throws IOException;

    @Override
    protected void doHead(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // On gere le fonctionement en interne !
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String uri = request.getRequestURI();

        String contextPath = request.getContextPath();
        if (contextPath != null) {
            uri = uri.substring(contextPath.length());
        }

        String servletPath = request.getServletPath();
        if (servletPath != null) {
            uri = uri.substring(servletPath.length());
        }

        int idx = uri.indexOf('/');
        if (idx >= 0) {
            uri = uri.substring(idx + 1);
        }

        boolean isVersioned = false;
        String version = null;
        if (getVersionSupport()) {
            idx = uri.indexOf('/');
            if (idx < 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            version = uri.substring(0, idx);
            uri = uri.substring(idx + 1);
            isVersioned = true;
        }

        URIParameters up = URIParameters.parseURI(uri);
        if (version == null) {
            version = up.getVersion();
        }
        String normalizedURI = up.getURI();

        if (version != null) {
            String repositoryVersion = repository.getVersion();
            if (repositoryVersion != null) {
                if (repositoryVersion.equals(version) == false) {
                    LOG.error("Not same repository version ! (current="
                            + repositoryVersion + " request=" + version + ")");

                    setNoCache(response);
                    response.sendError(HttpServletResponse.SC_CONFLICT,
                            "Invalid RCFaces version !");
                    return;
                }
            }
        }

        IFile file = repository.getFileByURI(normalizedURI);
        if (file == null) {
            setNoCache(response);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ICriteria criteria = constructCriteria(request, response, up);

        Record record = getFileRecord(file, criteria);

        sendRecord(request, response, record, isVersioned);
    }

    protected ICriteria constructCriteria(HttpServletRequest request,
            HttpServletResponse response, URIParameters up) {

        Locale locale = null;

        if (localeSupport) {
            String localeName = up.getLocaleName();
            if (localeName != null) {
                locale = convertLocaleName(localeName, true);
            }
        }

        if (locale == null) {
            locale = getDefaultLocale(request, response);
        }

        ICriteria localeCriteria = LocaleCriteria.get(locale);

        return localeCriteria;
    }

    protected abstract boolean getVersionSupport();

    protected Record getFileRecord(IFile file, ICriteria criteria) {
        Record record;

        if (criteria == null) {
            throw new FacesException("Locale is NULL for file '"
                    + file.getFilename() + "'.");
        }

        synchronized (fileToRecordByCriteria) {
            Map<IFile, Record> fileToRecord = fileToRecordByCriteria
                    .get(criteria);
            if (fileToRecord == null) {
                fileToRecord = new HashMap<IFile, Record>();
                fileToRecordByCriteria.put(criteria, fileToRecord);
            }

            record = fileToRecord.get(file);
            if (record == null) {
                record = newRecord(file, criteria);

                fileToRecord.put(file, record);
            }

            if (devMode) {
                record.verifyModifications();
            }
        }

        return record;
    }

    private void sendRecord(HttpServletRequest request,
            HttpServletResponse response, Record record, boolean isVersioned)
            throws IOException {

        byte buf[] = null;
        long modificationDate;
        boolean noHeader = false;
        String etag;
        String hash;

        synchronized (record) {
            etag = record.getETag();
            hash = record.getHash();

            modificationDate = record.getLastModificationDate();
            if (modificationDate > 0) {
                modificationDate -= (modificationDate % 1000);
            }

            if (hasGZipSupport()) {
                setVaryAcceptEncoding(response);
            }

            if (hasGZipSupport() && hasGzipSupport(request)) {
                byte jsGZip[] = record.getGZipedBuffer();
                if (jsGZip != null) {
                    setGzipContentEncoding(response, true);

                    buf = jsGZip;
                    noHeader = true;
                }
            }
            if (buf == null) {
                buf = record.getBuffer();
            }
        }

        if (buf == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set no cache for response.");
            }

            setNoCache(response);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (noCache) {
            setNoCache(response);

        } else {
            if (modificationDate > 0) {
                response.setDateHeader(HTTP_LAST_MODIFIED, modificationDate);
            }
            ExpirationDate expirationDate = record.getExpirationDate();
            if (expirationDate == null) {
                expirationDate = getDefaultExpirationDate(isVersioned);
            }

            if (expirationDate != null) {
                expirationDate.sendExpires(response);
            }
        }

        boolean different = false;

        if (different == false && etag != null) {
            String ifETag = request.getHeader(HTTP_IF_NONE_MATCH);
            if (ifETag != null) {
                if (etag.equals(ifETag)) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }

                different = true;
            }
        }

        if (different == false && hash != null) {
            String isHash = request.getHeader(HTTP_IF_NOT_HASH);
            if (isHash != null) {
                if (hash.equals(isHash)) {
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
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }
        }

        // String acceptCharset=request.getHeader("Accept-Charset");

        String contentType = getContentType(record);
        response.setContentType(contentType);

        if (etag != null) {
            response.setHeader(HTTP_ETAG, etag);
        }

        if (hash != null) {
            response.setHeader(HTTP_HASH, hash);
        }

        byte prolog[] = null;
        byte epilog[] = null;
        int length = buf.length;

        if (noHeader == false) {
            prolog = record.getProlog();
            if (prolog != null) {
                length += prolog.length;
            }

            epilog = record.getEpilog();
            if (epilog != null) {
                length += epilog.length;
            }
        }

        response.setContentLength(length);

        if (request.getMethod().equals(HEAD_HTTP_METHOD)) {
            return;
        }

        OutputStream outputStream = response.getOutputStream();
        if (prolog != null) {
            outputStream.write(prolog);
        }

        outputStream.write(buf);

        if (epilog != null) {
            outputStream.write(epilog);
        }
    }

    protected Record newRecord(IFile file, ICriteria criteria) {
        return new Record(file, criteria);
    }

    protected abstract String getContentType(Record record);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    protected class Record {

        protected final IFile file;

        protected final ICriteria criteria;

        protected byte buffer[];

        private byte gzippedBuffer[];

        private long lastModificationDate;

        protected ExpirationDate expirationDate;

        private String etag;

        private String hash;

        public Record(IFile file, ICriteria criteria) {
            this.file = file;
            this.criteria = criteria;
        }

        protected final IFile getFile() {
            return file;
        }

        public ExpirationDate getExpirationDate() {
            return expirationDate;
        }

        public void verifyModifications() {

            boolean modified = verifyFileModifications();

            if (modified == false) {
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Modification detected: " + getFile());
            }
            resetRecord();
        }

        protected void resetRecord() {
            buffer = null;
            gzippedBuffer = null;
            lastModificationDate = 0;
            etag = null;
            hash = null;

        }

        private boolean verifyFileModifications() {

            IContentRef[] urls = getFileContentReferences(file);

            IContentProvider contentProvider = file.getContentProvider();
            for (int i = 0; i < urls.length; i++) {
                long l;
                try {
                    IContent content = contentProvider.getContent(urls[i]);
                    try {
                        l = content.getLastModified();

                    } finally {
                        content.release();
                    }

                } catch (IOException ex) {
                    LOG.error("Can not get lastModified date of '" + urls[i]
                            + "'.", ex);
                    l = -1;
                }

                if (l < 1) {
                    l = System.currentTimeMillis();
                }

                LOG.debug("Verify file '" + file.getFilename() + "' delta="
                        + (l - lastModificationDate) + " ms.");

                if (lastModificationDate == l) {
                    return false;
                }

                if (lastModificationDate > 0) {
                    LOG.debug("File '" + file.getFilename()
                            + "' has been modified !");
                }
            }

            return true;
        }

        protected boolean verifyFilesModifications(IFile[] files) {
            boolean modified = false;

            for (int i = 0; i < files.length; i++) {
                Record record = getFileRecord(files[i], criteria);

                if (record.verifyFileModifications()) {
                    record.resetRecord();
                    modified = true;
                }
            }

            return modified;
        }

        public byte[] getBuffer() throws IOException {
            if (buffer != null) {
                return buffer;
            }

            IContentRef[] contentRefs = getFileContentReferences(file);

            ByteBufferOutputStream bos = new ByteBufferOutputStream(
                    CONTENT_INITIAL_SIZE);
            lastModificationDate = -1;

            for (int i = 0; i < contentRefs.length; i++) {
                IContent content = file.getContentProvider().getContent(
                        contentRefs[i]);
                try {
                    long date = content.getLastModified();
                    if (date < 1) {
                        date = System.currentTimeMillis();
                    }
                    if (lastModificationDate < date) {
                        lastModificationDate = date;
                    }

                    long size = content.getLength();
                    if (size == 0) {
                        continue;
                    }

                    InputStream in = content.getInputStream();
                    try {
                        byte buf[];
                        if (size > 0) {
                            buf = new byte[(int) size];

                        } else {
                            buf = new byte[4096];
                        }

                        for (;;) {
                            int ret = in.read(buf);
                            if (ret < 0) {
                                break;
                            }
                            bos.write(buf, 0, ret);
                        }

                    } finally {
                        try {
                            in.close();

                        } catch (Exception ex) {
                            LOG.error("Can not close inputstream '"
                                    + contentRefs[i] + "'.", ex);
                        }
                    }
                } finally {
                    content.release();
                }
            }

            bos.close();

            buffer = bos.toByteArray();

            int beforeUpdate = buffer.length;

            buffer = updateBuffer(buffer);

            if (LOG.isInfoEnabled()) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance(
                        DateFormat.SHORT, DateFormat.MEDIUM);

                LOG.debug("Load record '"
                        + file.getFilename()
                        + "' into "
                        + buffer.length
                        + " bytes, modified date="
                        + dateFormat.format(new Date(lastModificationDate))
                        + ((beforeUpdate > 0) ? ("  (update-ratio "
                                + (buffer.length * 100 / beforeUpdate) + "%)")
                                : ""));
            }

            if (hasEtagSupport()) {
                etag = computeETag(buffer);
            }

            if (hasHashSupport()) {
                hash = computeHash(buffer);
            }

            return buffer;
        }

        protected byte[] getFilesBuffer(IFile files[]) throws IOException {
            byte buffers[][] = new byte[files.length][];
            int size = 0;
            lastModificationDate = 0;

            for (int i = 0; i < files.length; i++) {
                Record record = getFileRecord(files[i], criteria);

                synchronized (record) {
                    buffers[i] = record.getBuffer();

                    size += buffers[i].length;

                    long lm = record.getLastModificationDate();

                    // plusieurs cas :
                    // * on ne connait encore aucune date
                    // * une des dates est inconnue, la date globale est
                    // donc inconnue
                    // * la nouvelle date est plus recente que les
                    // precedentes
                    if (lm > lastModificationDate) {
                        lastModificationDate = lm;
                    }
                }
            }

            buffer = new byte[size];
            int offset = 0;
            for (int i = 0; i < files.length; i++) {
                byte b[] = buffers[i];

                System.arraycopy(b, 0, buffer, offset, b.length);
                offset += b.length;
            }

            if (LOG.isInfoEnabled()) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance(
                        DateFormat.SHORT, DateFormat.MEDIUM);

                StringAppender sb = new StringAppender(files.length * 32);
                for (int i = 0; i < files.length; i++) {
                    Record record = getFileRecord(files[i], criteria);

                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(record.file.getFilename());
                }

                LOG.debug("Merge records for '" + file.getFilename()
                        + "' into " + buffer.length + " bytes, modified date="
                        + dateFormat.format(new Date(lastModificationDate))
                        + ", files '" + sb.toString() + "'.");
            }

            if (hasEtagSupport()) {
                etag = computeETag(buffer);
            }

            if (hasHashSupport()) {
                hash = computeHash(buffer);
            }

            return buffer;
        }

        protected IContentRef[] getFileContentReferences(IFile file) {
            return file.getContentReferences(criteria);
        }

        protected byte[] updateBuffer(byte[] buffer) throws IOException {
            return buffer;
        }

        public final long getLastModificationDate() throws IOException {
            getBuffer();

            return lastModificationDate;
        }

        public final String getETag() throws IOException {
            getBuffer();

            return etag;
        }

        public final String getHash() throws IOException {
            getBuffer();

            return hash;
        }

        public final byte[] getGZipedBuffer() throws IOException {
            if (gzippedBuffer != null) {
                return gzippedBuffer;
            }

            byte buf[] = getBuffer();
            if (buf == null || buf.length < 1) {
                return buf;
            }

            ByteBufferOutputStream bos = new ByteBufferOutputStream(buf.length);
            GZIPOutputStream gzos = new GZIPOutputStream(bos, buf.length);

            byte prolog[] = getProlog();
            if (prolog.length > 0) {
                gzos.write(prolog);
            }

            gzos.write(buf);

            byte epilog[] = getEpilog();
            if (epilog.length > 0) {
                gzos.write(epilog);
            }
            gzos.close();

            gzippedBuffer = bos.toByteArray();

            LOG.debug("GZIP record '" + file.getFilename() + "' into "
                    + gzippedBuffer.length + " bytes (compression-ratio "
                    + (gzippedBuffer.length * 100 / buffer.length)
                    + "% , original size=" + buffer.length + " bytes)");

            return gzippedBuffer;
        }

        public byte[] getProlog() throws IOException {
            return BYTE_EMPTY_ARRAY;
        }

        public byte[] getEpilog() throws IOException {
            return BYTE_EMPTY_ARRAY;
        }

        public String getCharset() {
            return null;
        }

        @Override
        public String toString() {
            return "[Record file='"
                    + file
                    + "' expiration='"
                    + expirationDate
                    + "' lastModication='"
                    + lastModificationDate
                    + "' buffer.size="
                    + ((buffer == null) ? "null" : String
                            .valueOf(buffer.length)) + "]";
        }

    }
}
