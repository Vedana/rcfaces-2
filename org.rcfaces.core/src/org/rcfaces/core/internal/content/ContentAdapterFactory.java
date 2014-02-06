/*
 * $Id: ContentAdapterFactory.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.contentStorage.AbstractResolvedContent;
import org.rcfaces.core.internal.contentStorage.IResolvedContent;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.Base64;
import org.rcfaces.core.internal.util.MessageDigestSelector;
import org.rcfaces.core.lang.IAdapterFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class ContentAdapterFactory implements IAdapterFactory {

    private static final Log LOG = LogFactory
            .getLog(ContentAdapterFactory.class);

    private static final String TEMP_PREFIX = "contentAdapter_";

    protected static final Map<String, String> suffixByMimeType = new HashMap<String, String>(
            8);

    protected static final FileNameMap fileNameMap = URLConnection
            .getFileNameMap();

    private static final long startTimeMillis = System.currentTimeMillis();

    private final Object tempFileCounterLock = new Object();

    private long tempFileCounter;

    public <T> T getAdapter(Object adaptableObject, Class<T> adapterType,
            Object parameter) {

        return null;
    }

    public static String getSuffixByMimeType(String contentType) {
        return suffixByMimeType.get(contentType.toLowerCase());
    }

    public Class< ? >[] getAdapterList() {
        return new Class[] { IResolvedContent.class };
    }

    public File createTempFile(String contentType, String suffix)
            throws IOException {
        File file = null;

        File tempFolder = getTempFolder();
        if (tempFolder != null) {

            for (;;) {
                long id;
                synchronized (tempFileCounterLock) {
                    id = tempFileCounter++;
                }

                file = new File(tempFolder, getTempPrefix() + startTimeMillis
                        + "_" + id + "." + suffix);

                if (file.createNewFile()) {
                    break;
                }
            }
        }

        if (file == null) {
            file = File.createTempFile(getTempPrefix(), "." + suffix);
        }

        file.deleteOnExit();

        return file;
    }

    protected String getTempPrefix() {
        return TEMP_PREFIX;
    }

    protected File getTempFolder() {
        return null;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected class FileResolvedContent extends AbstractResolvedContent
            implements Serializable {

        private static final long serialVersionUID = 2045867975901327708L;

        private final String contentType;

        private final String suffix;

        private final int length;

        private final long lastModificationDate;

        private final String specifiedResourceKey;

        private String etag;

        private String hashCode;

        private byte fileSerialized[];

        private transient File file;

        @SuppressWarnings("unused")
        public FileResolvedContent(String contentType, String suffix,
                File file, String specifiedResourceKey, long lastModifiedDate) {
            this.file = file;
            this.contentType = contentType;
            this.suffix = suffix;
            this.specifiedResourceKey = specifiedResourceKey;

            this.length = (int) file.length();
            this.lastModificationDate = lastModifiedDate;

            if (Constants.ETAG_SUPPORT || Constants.HASH_SUPPORT) {
                computeHashCodes();
            }
        }

        public String getContentType() {
            return contentType;
        }

        @Override
        public String getURLSuffix() {
            return suffix;
        }

        @Override
        public String getResourceKey() {
            if (specifiedResourceKey == null) {
                return specifiedResourceKey;
            }

            return super.getResourceKey();
        }

        public InputStream getInputStream() throws IOException {
            if (fileSerialized != null) {
                return new ByteArrayInputStream(fileSerialized);
            }

            return new FileInputStream(file);
        }

        @Override
        public long getModificationDate() {
            return lastModificationDate;
        }

        @Override
        public int getLength() {
            return length;
        }

        @Override
        public String getETag() {
            return etag;
        }

        @Override
        public String getHash() {
            return hashCode;
        }

        @Override
        protected void finalize() throws Throwable {
            if (file != null) {
                try {
                    file.delete();
                    file = null;

                } catch (Throwable ex) {
                    LOG.error("Can not delete file '" + file + "'.", ex);
                }
            }
            super.finalize();
        }

        protected void computeHashCodes() {
            MessageDigest etagMessageDigest = null;
            MessageDigest hashMessageDigest = null;

            if (Constants.ETAG_SUPPORT) {
                etagMessageDigest = MessageDigestSelector
                        .getInstance(Constants.ETAG_DIGEST_ALGORITHMS);
            }

            if (Constants.HASH_SUPPORT) {
                hashMessageDigest = MessageDigestSelector
                        .getInstance(Constants.HASH_DIGEST_ALGORITHMS);
            }

            if (hashMessageDigest == null && etagMessageDigest == null) {
                return;
            }

            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file);

            } catch (FileNotFoundException e) {
                LOG.error("Can not compute Etag and Hashcode for file '" + file
                        + "'." + e);
                return;
            }

            try {
                byte buffer[] = new byte[4096];

                for (;;) {
                    int ret = fileInputStream.read(buffer);
                    if (ret < 1) {
                        break;
                    }

                    if (etagMessageDigest != null) {
                        etagMessageDigest.update(buffer, 0, ret);
                    }

                    if (hashMessageDigest != null) {
                        hashMessageDigest.update(buffer, 0, ret);
                    }
                }

                if (etagMessageDigest != null) {
                    byte etagDigest[] = etagMessageDigest.digest();

                    StringAppender sb = new StringAppender(
                            etagDigest.length * 2 + 16);
                    sb.append("\"rcfaces:");
                    for (int i = 0; i < etagDigest.length; i++) {
                        int v = etagDigest[i] & 0xff;
                        if (v < 16) {
                            sb.append('0');
                        }
                        sb.append(Integer.toHexString(v));
                    }

                    sb.append(':');
                    sb.append(Integer.toHexString(length));

                    sb.append('\"');
                    etag = sb.toString();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Etag for file '" + file + "' = " + etag);
                    }
                }

                if (hashMessageDigest != null) {
                    byte hashDigest[] = hashMessageDigest.digest();

                    hashCode = Base64.encodeBytes(hashDigest,
                            Base64.DONT_BREAK_LINES);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Hashcode for file '" + file + "' = "
                                + hashCode);
                    }
                }

            } catch (IOException e) {
                LOG.error("Can not compute Etag and Hashcode for file '" + file
                        + "'.", e);

            } finally {
                try {
                    fileInputStream.close();

                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }

        private void writeObject(java.io.ObjectOutputStream s)
                throws java.io.IOException {

            if (file != null) {
                fileSerialized = new byte[length];

                FileInputStream fin = new FileInputStream(file);
                try {
                    fin.read(fileSerialized);

                } catch (IOException ex) {
                    LOG.error(ex);

                } finally {
                    try {
                        fin.close();

                    } catch (IOException ex) {
                        LOG.error(ex);
                    }
                }

                file = null;
            }

            s.defaultWriteObject();
        }
    }
}
