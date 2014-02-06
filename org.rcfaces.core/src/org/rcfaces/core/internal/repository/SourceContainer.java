/*
 * $Id: SourceContainer.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.io.BufferedInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.codec.SourceFilter;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.webapp.ExtendedHttpServlet;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public abstract class SourceContainer<T> {

    private static final Log LOG = LogFactory.getLog(SourceContainer.class);

    protected static final String EXTERNAL_REPOSITORIES_CONFIG_NAME = "external.repositories";

    private static final int BUFFER_INITIAL_SIZE = 16000;

    private final ServletConfig servletConfig;

    private final String charSet;

    private final boolean canUseGzip;

    private final boolean canUseETag;

    private final boolean canUseHash;

    private final Set<String> modules;

    private final String externalRepositoriesPropertyName;

    private final String repositoryVersion;

    private final String repositoryType;

    private final Map<T, IParameterizedContent<T>> contentByParameters = new HashMap<T, IParameterizedContent<T>>();

    // private byte[] sourceBufferExternalGZip = null;

    public SourceContainer(ServletConfig config, String repositoryType,
            Set<String> modules, String charSet, boolean canUseGzip,
            boolean canUseETag, boolean canUseHash,
            String externalRepositoriesPropertyName, String repositoryVersion)
            throws ServletException {
        this.servletConfig = config;
        this.repositoryType = repositoryType;
        this.canUseGzip = canUseGzip;
        this.canUseETag = canUseETag;
        this.canUseHash = canUseHash;
        this.charSet = charSet;
        this.modules = modules;
        this.repositoryVersion = repositoryVersion;
        this.externalRepositoriesPropertyName = externalRepositoriesPropertyName;

        flush();
    }

    public final String getCharSet() {
        return charSet;
    }

    public final boolean isCanUseGzip() {
        return canUseGzip;
    }

    public final boolean isCanUseETag() {
        return canUseETag;
    }

    public final boolean isCanUseHash() {
        return canUseHash;
    }

    protected abstract T noParameter();

    protected StringAppender postConstructBuffer(
            BasicParameterizedContent parameterizedBuffer, StringAppender buffer) {

        if (canSkipSpace()) {
            return new StringAppender(SourceFilter.filterSkipSpaces(buffer
                    .toString()));
        }

        if (canRemoveComments()) {
            return new StringAppender(SourceFilter.filter(buffer.toString()));
        }

        return buffer;
    }

    protected StringAppender preConstructBuffer(
            BasicParameterizedContent parameterizedBuffer, StringAppender buffer) {
        return buffer;
    }

    public String getVersion() {
        return repositoryVersion;
    }

    protected RcfacesContext getRcfacesContext() {
        return RcfacesContext.getInstance(servletConfig.getServletContext(),
                null, null);
    }

    protected URL getURL(String path) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting URL of '" + path + "'");
        }

        String pr = path;
        if (pr.charAt(0) != '/') {
            pr = "/" + pr;
        }

        try {
            URL url = servletConfig.getServletContext().getResource(pr);

            if (LOG.isDebugEnabled()) {
                LOG.debug("URL resource of servletContext '" + path + "' => "
                        + url);
            }

            if (url != null) {
                try {
                    InputStream in = url.openStream();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Open stream resource of servletContext '"
                                + url + "' => " + in);
                    }

                    if (in != null) {
                        try {
                            in.close();

                        } catch (IOException ex) {
                            // Probleme � la fermeture ?
                        }

                        return url;
                    }
                } catch (IOException ex) {
                    // Rien ...
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("IOException for servletContext resource '"
                                + url + "'", ex);
                    }
                }
            }

        } catch (MalformedURLException e) {
            // Rien ...
            if (LOG.isDebugEnabled()) {
                LOG.debug("Malformed url for servletContext '" + path + "'", e);
            }
        }

        pr = path;
        if (pr.charAt(0) == '/') {
            pr = pr.substring(1);
        }
        URL url = getClass().getClassLoader().getResource(pr);

        if (LOG.isDebugEnabled()) {
            LOG.debug("URL resource of classLoader '" + path + "' => " + url);
        }

        if (url != null) {
            try {
                InputStream in = url.openStream();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Open stream resource of classLoader '" + url
                            + "' => " + in);
                }

                if (in != null) {
                    try {
                        in.close();

                    } catch (IOException ex) {
                        // Probleme � la fermeture ?
                    }

                    return url;
                }
            } catch (IOException ex) {
                // Rien ...
                if (LOG.isDebugEnabled()) {
                    LOG.debug("IOException for classLoader resource '" + url
                            + "'", ex);
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("No URL found for path '" + path + "'");
        }

        return null;
    }

    private List<SourceFile> listExternalFiles(InputStream inputStream,
            String source, boolean nameAttribute) {

        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);

        digester.setEntityResolver(new EntityResolver() {

            public InputSource resolveEntity(String string, String string1) {
                return new InputSource(new CharArrayReader(new char[0]));
            }

        });

        final List<SourceFile> list = new ArrayList<SourceFile>();

        if (nameAttribute) {
            final String[] baseDirectoryRef = new String[1];

            digester.addRule("repository", new Rule() {

                @Override
                public void begin(String namespace, String name,
                        Attributes attributes) {

                    String baseDirectory = attributes.getValue("baseDirectory");

                    if (baseDirectory != null && baseDirectory.length() > 0) {
                        baseDirectoryRef[0] = baseDirectory;
                    }
                }
            });

            final boolean[] filtred = new boolean[1];

            digester.addRule("repository/module", new Rule() {

                @Override
                public void begin(String namespace, String name,
                        Attributes attributes) {

                    String id = attributes.getValue("id");

                    if (modules != null) {
                        filtred[0] = (id != null && modules.contains(id) == false);

                    } else {
                        filtred[0] = false;
                    }
                }
            });

            digester.addRule("repository/module/file", new Rule() {

                @Override
                public void begin(String namespace, String name,
                        Attributes attributes) {

                    if (filtred[0]) {
                        return;
                    }

                    SourceFile file = createSourceFile(baseDirectoryRef[0],
                            null, attributes);
                    if (file != null) {
                        list.add(file);
                    }
                }

            });

        } else {
            digester.addRule("repository/file", new Rule() {

                @Override
                public void body(String namespace, String name, String text) {
                    SourceFile file = createSourceFile(null, text, null);
                    if (file != null) {
                        list.add(file);
                    }
                }
            });
        }

        try {
            digester.parse(inputStream);

        } catch (Exception e) {
            LOG.error("Can not parse '" + source + "'", e);
        }

        return list;
    }

    protected SourceFile createSourceFile(String baseDirectory, String body,
            Attributes attributes) {

        if (attributes != null) {
            String fileName = attributes.getValue("name");

            if (fileName != null && fileName.length() > 0) {
                if (baseDirectory != null) {
                    fileName = baseDirectory + "/" + fileName;
                }

                SourceFile sourceFile = newSourceFile();

                sourceFile.setFileName(fileName);

                return sourceFile;
            }

            return null;
        }

        if (body != null && body.trim().length() > 0) {
            String fileName = body.trim();
            if (baseDirectory != null) {
                fileName = baseDirectory + "/" + fileName;
            }

            SourceFile sourceFile = newSourceFile();

            sourceFile.setFileName(fileName);

            return sourceFile;
        }

        return null;
    }

    protected SourceFile newSourceFile() {
        return new SourceFile();
    }

    protected boolean canSkipSpace() {
        return false;
    }

    protected boolean canRemoveComments() {
        return false;
    }

    public synchronized void flush() throws ServletException {
        synchronized (contentByParameters) {
            contentByParameters.clear();
        }
    }

    protected void error(String message, Throwable th) {
        message = "RCFaces.SourceRepository: " + message;

        LOG.error(message, th);

        if (th == null) {
            servletConfig.getServletContext().log(message);
            return;
        }

        servletConfig.getServletContext().log(message, th);
    }

    public IParameterizedContent<T> getDefaultContent() throws ServletException {
        return getContent(noParameter());
    }

    public IParameterizedContent<T> getContent(T parameter)
            throws ServletException {

        IParameterizedContent<T> parameterizedContent;
        synchronized (contentByParameters) {
            parameterizedContent = contentByParameters.get(parameter);

            if (parameterizedContent == null) {
                parameterizedContent = createParameterizedContent(parameter);

                contentByParameters.put(parameter, parameterizedContent);
            }
        }

        parameterizedContent.initialize();

        return parameterizedContent;
    }

    protected IParameterizedContent<T> createParameterizedContent(T parameter) {
        return new BasicParameterizedContent(parameter);
    }

    protected void addURLContent(URLConnection urlConnection,
            StringAppender buffer) throws IOException {

        InputStream inputStream = urlConnection.getInputStream();

        try {
            char buf[] = new char[4096];

            Reader reader = new InputStreamReader(new BufferedInputStream(
                    inputStream, buf.length), charSet);

            for (;;) {
                int len = reader.read(buf, 0, buf.length);
                if (len < 1) {
                    break;
                }

                buffer.append(buf, 0, len);
            }

        } finally {
            inputStream.close();
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
     */
    public interface IParameterizedContent<T> {

        SourceContainer<T> getContainer();

        void initialize() throws ServletException;

        long getLastModified();

        String getETag();

        String getHash();

        byte[] getRawBuffer();

        byte[] getGZipedBuffer();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
     */
    protected class BasicParameterizedContent implements
            IParameterizedContent<T> {
        protected final T parameter;

        private boolean initialized;

        private StringAppender buffer;

        private byte[] sourceBuffer = null;

        private byte[] sourceBufferGZip = null;

        private long lastModified = -1;

        private String etag = null;

        private String hash = null;

        public BasicParameterizedContent(T parameter) {
            this.parameter = parameter;
        }

        public synchronized void initialize() throws ServletException {
            if (initialized) {
                return;
            }
            initialized = true;

            buffer = new StringAppender(BUFFER_INITIAL_SIZE);

            buffer = preConstructBuffer(this, buffer);

            addRepositoryFiles();

            if (externalRepositoriesPropertyName != null) {
                buffer = addExternalRepositories(buffer,
                        externalRepositoriesPropertyName);
            }

            buffer = postConstructBuffer(this, buffer);

            initializeBuffers();

            buffer = null;
        }

        public SourceContainer<T> getContainer() {
            return SourceContainer.this;
        }

        protected void updateLastModification(URLConnection urlConnection) {
            long lm = urlConnection.getLastModified();
            if (lm <= 0) {
                return;
            }

            if (lm <= lastModified) {
                return;
            }

            lastModified = lm;
        }

        protected void initializeBuffers() throws ServletException {

            try {
                sourceBuffer = buffer.getBytes(getCharSet());

            } catch (UnsupportedEncodingException e) {
                LOG.error(e);
                throw new ServletException(e);
            }

            if (lastModified <= 0) {
                lastModified = System.currentTimeMillis();
            }

            if (canUseETag) {
                etag = ExtendedHttpServlet.computeETag(sourceBuffer);
            }

            if (canUseHash) {
                hash = ExtendedHttpServlet.computeHash(sourceBuffer);
            }

            if (canUseGzip && sourceBuffer.length > 0) {
                try {
                    ByteBufferOutputStream bos = new ByteBufferOutputStream(
                            sourceBuffer.length);
                    GZIPOutputStream gzos = new GZIPOutputStream(bos,
                            sourceBuffer.length);
                    gzos.write(sourceBuffer);
                    gzos.close();

                    sourceBufferGZip = bos.toByteArray();

                    bos.close();

                } catch (IOException ex) {
                    throw new ServletException(
                            "Can not create GZIP buffer for " + repositoryType
                                    + " files.", ex);
                }
            }

            if (LOG.isInfoEnabled()) {
                String message = repositoryType + ": buffers loaded into "
                        + sourceBuffer.length + " bytes";
                if (sourceBufferGZip != null) {
                    message += " (GZiped: "
                            + sourceBufferGZip.length
                            + " ["
                            + (sourceBufferGZip.length * 100 / sourceBuffer.length)
                            + "%])";
                }

                LOG.info(message, null);
            }
        }

        protected final StringAppender addExternalRepositories(
                StringAppender buffer, String propertName) {
            if (propertName == null) {
                return buffer;
            }

            String path = servletConfig.getInitParameter(propertName);
            if (path == null) {
                return buffer;
            }

            StringTokenizer st = new StringTokenizer(path, ";,");
            for (; st.hasMoreTokens();) {
                String repositoryPath = st.nextToken();

                parseXMLRepository(repositoryPath, false);
            }

            return buffer;
        }

        protected final void parseXMLRepository(String path,
                boolean repositoryFormal) {
            URL url = getURL(path);
            if (url == null) {
                error("Can not get URL for path '" + path + "'.", null);
                return;
            }

            URLConnection urlConnection;
            try {
                urlConnection = url.openConnection();

            } catch (IOException ex) {
                error("Can not get content of '" + url + "'.", ex);

                return;
            }

            if (urlConnection == null) {
                return;
            }

            updateLastModification(urlConnection);

            InputStream inputStream;
            try {
                inputStream = urlConnection.getInputStream();

            } catch (IOException ex) {
                error("Can not open '" + url + "'.", ex);

                return;
            }

            if (inputStream == null) {
                return;
            }

            List<SourceFile> files;
            try {
                files = listExternalFiles(inputStream, path, repositoryFormal);

            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }

            if (files != null) {
                files = filterFiles(files);
            }

            if (files == null || files.isEmpty() == true) {
                return;
            }

            Set<URL> alreadyDone = new HashSet<URL>(files.size());
            for (SourceFile sourceFile : files) {
                URL fileURL = getURL(sourceFile.getFileName());
                if (fileURL == null) {
                    error("Can not get URL for path '" + sourceFile + "'.",
                            null);
                    continue;
                }

                if (alreadyDone.add(fileURL) == false) {
                    LOG.error("ALERT: URL '" + fileURL + "' is added TWICE !");
                }

                try {
                    addURLContent(fileURL, buffer);

                } catch (IOException e) {
                    error("Can not append external file '" + fileURL + "'.", e);
                }
            }
        }

        protected void addURLContent(URL url, StringAppender buffer)
                throws IOException {

            URLConnection urlConnection = url.openConnection();

            LOG.debug("Load URL content '" + url + "' (charset=" + charSet
                    + ").");

            updateLastModification(urlConnection);

            SourceContainer.this.addURLContent(urlConnection, buffer);
        }

        protected List<SourceFile> filterFiles(List<SourceFile> files) {
            return files;
        }

        protected void addRepositoryFiles() throws ServletException {

            RcfacesContext rcfacesContext = getRcfacesContext();

            String repositoriesPaths[] = rcfacesContext.getRepositoryManager()
                    .listRepositoryLocations(repositoryType);

            for (int j = 0; j < repositoriesPaths.length; j++) {
                String repositoryPath = repositoriesPaths[j];

                parseXMLRepository(repositoryPath, true);
            }
        }

        public long getLastModified() {
            return lastModified;
        }

        public String getETag() {
            return etag;
        }

        public String getHash() {
            return hash;
        }

        public byte[] getRawBuffer() {
            return sourceBuffer;
        }

        public byte[] getGZipedBuffer() {
            return sourceBufferGZip;
        }
    }

    protected class SourceFile {
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return "[SourceFile fileName='" + fileName + "']";
        }
    }
}
