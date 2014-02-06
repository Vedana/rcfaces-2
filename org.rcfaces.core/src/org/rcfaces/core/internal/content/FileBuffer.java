/*
 * $Id: FileBuffer.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class FileBuffer implements IFileBuffer {

    private static final Log LOG = LogFactory.getLog(FileBuffer.class);

    private static final String TEMP_FILE_PREFIX = "fileBuffer_";

    private static final String TEMP_FILE_SUFFIX;
    static {
        TEMP_FILE_SUFFIX = "." + Constants.getVersion() + ".dat";
    }

    private static boolean securityError;

    private final String bufferName;

    private File file;

    private String hash;

    private String etag;

    private int size;

    private long lastModified;

    private String contentType;

    private boolean errored;

    private String redirectedURL;

    public FileBuffer(String bufferName) {
        this.bufferName = bufferName;
    }

    public boolean isInitialized() {
        return contentType != null;
    }

    public String getRedirection() {
        return redirectedURL;
    }

    public void initializeRedirection(String url) {
        this.redirectedURL = url;
    }

    public void initialize(String contentType, byte buffer[], long lastModified)
            throws IOException {

        if (LOG.isTraceEnabled()) {
            LOG.trace("Initialize fileBuffer '" + bufferName + "'.");
        }

        this.contentType = contentType;

        try {
            file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

        } catch (IOException ex) {
            LOG.error("Can not create temp file for file buffer ! (bufferName="
                    + bufferName + ")", ex);

            throw ex;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Temp file associated to '" + bufferName + "' is "
                    + file.getAbsolutePath());
        }

        try {
            file.deleteOnExit();

        } catch (SecurityException ex) {
            if (securityError == false) {
                securityError = true;

                LOG.error(ex);
            }
        }

        hash = ExtendedHttpServlet.computeHash(buffer);
        etag = ExtendedHttpServlet.computeETag(buffer);
        this.size = buffer.length;

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(buffer);

        } catch (IOException ex) {
            LOG.error("Can not write temp file for file buffer ! (bufferName="
                    + bufferName + ")", ex);

            throw ex;

        } finally {
            fileOutputStream.close();
        }

        this.lastModified = lastModified;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Store file buffer '" + bufferName + "' into " + size
                    + " bytes. (disk location='" + file.getAbsolutePath()
                    + "')");
        }
    }

    public int getSize() {
        return size;
    }

    public InputStream getContent() throws IOException {
        return new FileInputStream(file);
    }

    public String getName() {
        return bufferName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getModificationDate() {
        return lastModified;
    }

    public String getHash() {
        return hash;
    }

    public String getETag() {
        return etag;
    }

    public boolean isErrored() {
        return errored;
    }

    public void setErrored() {
        errored = true;
    }

    @Override
    public String toString() {
        return "[FileBuffer name='" + bufferName + "' errored=" + errored
                + " file='" + file + "' contentType='" + contentType
                + "' lastModified=" + lastModified + " size=" + size + "]";
    }

    @Override
    protected void finalize() throws Throwable {
        if (file != null) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Finalize file buffer, delete file='"
                        + file.getAbsolutePath() + "'.");
            }

            try {
                file.delete();

            } catch (Throwable th) {
                LOG.error("Can not delete file '" + file.getAbsolutePath()
                        + "'.", th);
            }
            file = null;
        }

        super.finalize();
    }

}
