/*
 * $Id: GZipedResolvedContent.java,v 1.4 2013/11/13 12:53:20 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentStorage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.Base64;
import org.rcfaces.core.internal.util.MessageDigestSelector;
import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:20 $
 */
public class GZipedResolvedContent implements IResolvedContent, Serializable {

    private static final long serialVersionUID = -6336824522228175406L;

    private static final Log LOG = LogFactory
            .getLog(GZipedResolvedContent.class);

    private enum GzipedState {
        NOT_GZIPED, GZIPED, GZIP_ERROR, GZIP_NONE
    }

    private static final String TEMP_FILE_PREFIX = "gzippedFile_";

    private static final String TEMP_FILE_SUFFIX;

    private static final int BUFFER_SIZE = 1024 * 32;

    static {
        TEMP_FILE_SUFFIX = "." + Constants.getVersion() + ".dat";
    }

    private final IResolvedContent source;

    private String hash;

    private int length;

    private transient GzipedState gzipState = GzipedState.NOT_GZIPED;

    private transient File gzippedFile;

    public GZipedResolvedContent(IResolvedContent source) {
        this.source = source;
    }

    public void appendHashInformations(StringAppender sa) {
        source.appendHashInformations(sa);
    }

    public String getContentType() {
        return source.getContentType();
    }

    public String getETag() {
        return source.getETag();
    }

    public long getModificationDate() {
        return source.getModificationDate();
    }

    public String getResourceKey() {
        return source.getResourceKey();
    }

    public String getURLSuffix() {
        return source.getURLSuffix();
    }

    public boolean isErrored() {
        return source.isErrored();
    }

    public boolean isProcessAtRequest() {
        return source.isProcessAtRequest();
    }

    public boolean isVersioned() {
        return source.isVersioned();
    }

    public void setVersioned(boolean versioned) {
        // source.setVersioned(versioned);
        throw new IllegalStateException(
                "Can not set version of gziped resolvedContent");
    }

    public boolean isGzipped() {
        return getGZipState() == GzipedState.GZIPED;
    }

    public String getHash() {
        if (getGZipState() != GzipedState.GZIPED) {
            return source.getHash();
        }

        return hash;
    }

    public InputStream getInputStream() throws IOException {
        if (getGZipState() != GzipedState.GZIPED) {
            return source.getInputStream();
        }

        return new BufferedInputStream(new FileInputStream(gzippedFile),
                BUFFER_SIZE);
    }

    public int getLength() {
        if (getGZipState() != GzipedState.GZIPED) {
            return source.getLength();
        }

        return length;
    }

    public String getContentEncoding() {
        if (getGZipState() != GzipedState.GZIPED) {
            return null;
        }

        return ExtendedHttpServlet.GZIP_CONTENT_ENCODING;
    }

    private synchronized GzipedState getGZipState() {
        if (gzipState != GzipedState.NOT_GZIPED) {
            return gzipState;
        }

        if (source.getLength() < Constants.MINIMUM_GZIP_BUFFER_SIZE) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Buffer is too small to try compression ! ("
                        + source.getLength() + "<"
                        + Constants.MINIMUM_GZIP_BUFFER_SIZE + ")");
            }

            gzipState = GzipedState.GZIP_NONE;
            return gzipState;
        }

        MessageDigest hashMessageDigest = null;
        if (Constants.HASH_SUPPORT) {
            hashMessageDigest = MessageDigestSelector
                    .getInstance(Constants.HASH_DIGEST_ALGORITHMS);
        }

        try {
            gzippedFile = File.createTempFile(TEMP_FILE_PREFIX,
                    TEMP_FILE_SUFFIX);

            GZIPOutputStream outputStream = new GZIPOutputStream(
                    new FileOutputStream(gzippedFile));
            try {
                InputStream inputStream = source.getInputStream();

                try {
                    byte buffer[] = new byte[8192];

                    for (;;) {
                        int ret = inputStream.read(buffer);
                        if (ret < 1) {
                            break;
                        }

                        outputStream.write(buffer, 0, ret);

                        if (hashMessageDigest != null) {
                            hashMessageDigest.update(buffer, 0, ret);
                        }
                    }

                } finally {
                    inputStream.close();
                }

            } finally {
                outputStream.close();
            }

            length = (int) gzippedFile.length();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Gzip content '" + getResourceKey() + "' in "
                        + length + " bytes.");
            }

            if (hashMessageDigest != null) {
                byte hashDigest[] = hashMessageDigest.digest();

                hash = Base64.encodeBytes(hashDigest, Base64.DONT_BREAK_LINES);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Hashcode for file '" + getResourceKey() + "' = "
                            + hash);
                }
            }

            gzipState = GzipedState.GZIPED;

        } catch (IOException ex) {
            LOG.error("Can not gzip content '" + getResourceKey() + "'.", ex);

            gzipState = GzipedState.GZIP_ERROR;
        }

        return gzipState;
    }

    @Override
    protected void finalize() throws Throwable {
        if (gzippedFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Finalize gzipped content, delete file='"
                        + gzippedFile.getAbsolutePath() + "'.");
            }

            try {
                gzippedFile.delete();

            } catch (Throwable th) {
                LOG.error(
                        "Can not delete file '" + gzippedFile.getAbsolutePath()
                                + "'.", th);
            }
            gzippedFile = null;
        }

        super.finalize();
    }
}
