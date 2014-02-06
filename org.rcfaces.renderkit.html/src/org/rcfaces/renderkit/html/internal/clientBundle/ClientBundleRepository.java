/*
 * $Id: ClientBundleRepository.java,v 1.5 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.clientBundle;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.FacesException;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.ByteBufferInputStream;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.AbstractContentRef;
import org.rcfaces.core.internal.repository.AbstractRepository;
import org.rcfaces.core.internal.repository.IContentRef;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.version.HashCodeTools;
import org.rcfaces.core.internal.webapp.URIParameters;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.util.JavaScriptResponseWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:32 $
 */
class ClientBundleRepository extends AbstractRepository implements
        IClientBundleRepository {

    private static final long serialVersionUID = -2753241645405120653L;

    private static final Log LOG = LogFactory
            .getLog(ClientBundleRepository.class);

    private static final int BUNDLE_BUFFER_INITIAL_SIZE = 9000;

    private static final boolean VERIFY_BUNDLE_KEY = true;

    private final IContentProvider bundleContentProvider = new IContentProvider() {

        public IContent getContent(IContentRef contentRef) {
            String baseName = ((ResourceContentRef) contentRef)
                    .getResourceFile().getFilename();

            ICriteria criteria = contentRef.getCriteria();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get resourceBundle name='" + baseName + "' locale='"
                        + criteria + "'.");
            }

            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();

            Locale locale = LocaleCriteria.getLocale(criteria);
            if (locale == null) {
                LOG.error("Can not get locale from criteria '" + criteria + "'");
                return null;
            }

            try {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(
                        baseName, locale, classLoader);

                return new ResourceContent(resourceBundle, baseName);

            } catch (MissingResourceException ex) {
                LOG.error("Can not find resource bundle: basename='" + baseName
                        + "' locale='" + criteria + "' classLoader='"
                        + classLoader + "'.", ex);
            }

            return null;
        }

        public IContentRef[] searchCriteriaContentReference(
                IContentRef contentReference, ICriteria criteria) {
            return null;
        }

    };

    private final ServletContext servletContext;

    public ClientBundleRepository(ServletContext servletContext,
            String servletURI, String repositoryVersion) {
        super(servletURI, repositoryVersion);

        this.servletContext = servletContext;
    }

    protected IContentProvider getDefaultContentProvider() {
        return bundleContentProvider;
    }

    public IFile getFileByName(String name) {
        return new ResourceFile(name);
    }

    public IFile getFileByURI(String uri) {
        if (uri.endsWith(".js") == false) {
            return null;
        }

        IFile file = getFileByName(uri.substring(0, uri.lastIndexOf('.')));

        return file;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:32 $
     */
    private class ResourceContent extends AbstractContent {
        private final byte buffer[];

        public ResourceContent(ResourceBundle resourceBundle, String baseName) {
            buffer = createBuffer(resourceBundle, baseName);
        }

        public InputStream getInputStream() {
            return new ByteBufferInputStream(buffer);
        }

        public long getLength() {
            return buffer.length;
        }

        public byte[] getByteArray() {
            return buffer;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.5 $ $Date: 2013/11/13 12:53:32 $
     */
    private final class ResourceFile implements IFile {

        private static final long serialVersionUID = -1983757389992719786L;

        private static final String ERROR_VERSION = "ERR";

        private final String baseName;

        private String noCriteriaURI;

        private Map<ICriteria, String> localizedUris;

        public ResourceFile(String baseName) {
            this.baseName = baseName;
        }

        public IContentProvider getContentProvider() {
            return bundleContentProvider;
        }

        public IContentRef[] getContentReferences(ICriteria criteria) {
            return new IContentRef[] { new ResourceContentRef(criteria, this) };
        }

        public String getFilename() {
            return baseName;
        }

        public IRepository getRepository() {
            return ClientBundleRepository.this;
        }

        public synchronized String getURI(ICriteria criteria) {
            if (criteria == null) {
                if (noCriteriaURI != null) {
                    return noCriteriaURI;
                }
            } else if (localizedUris != null) {
                String luri = localizedUris.get(criteria);
                if (luri != null) {
                    return luri;
                }
            }

            StringAppender sa = new StringAppender(256);

            sa.append(servletURI);

            if (Constants.VERSIONED_CLIENT_BUNDLE_SUPPORT) {
                sa.append('/');

                String hashCode = computeHashCode(criteria);

                if (hashCode == null) {
                    hashCode = ERROR_VERSION;
                }
                sa.append(hashCode);
            }

            sa.append('/');
            sa.append(baseName);
            sa.append(".js");

            String ret = sa.toString();
            if (criteria != null) {
                URIParameters p = URIParameters.parseURI(ret);

                criteria.appendSuffix(p);

                ret = p.computeParametredURI();
            }

            if (criteria == null) {
                noCriteriaURI = ret;
            } else {
                if (localizedUris == null) {
                    localizedUris = new HashMap<ICriteria, String>(4);
                }

                localizedUris.put(criteria, ret);
            }

            return ret;
        }

        private String computeHashCode(ICriteria criteria) {
            ResourceContent content = (ResourceContent) getContentProvider()
                    .getContent(new ResourceContentRef(criteria, this));
            if (content == null) {
                LOG.error("Can not get content of client bundle '"
                        + getFilename() + "' for criteria '" + criteria + "'.");

                return null;
            }

            return HashCodeTools
                    .computeURLFormat(
                            null,
                            null,
                            content.getByteArray(),
                            org.rcfaces.core.internal.Constants.VERSIONED_URI_HASHCODE_MAX_SIZE);

        }

        public IFile[] listDependencies() {
            return FILE_EMPTY_ARRAY;
        }
    }

    private class ResourceContentRef extends AbstractContentRef {

        private final ResourceFile resourceFile;

        protected ResourceContentRef(ICriteria criteria,
                ResourceFile resourceFile) {
            super(criteria);

            this.resourceFile = resourceFile;
        }

        public ResourceFile getResourceFile() {
            return resourceFile;
        }

    }

    private byte[] createBuffer(ResourceBundle resourceBundle, String baseName) {
        ByteBufferOutputStream out = new ByteBufferOutputStream(
                BUNDLE_BUFFER_INITIAL_SIZE);

        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(out,
                    ClientResourceBundleServlet.RESOURCE_BUNDLE_ENCODING);

        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex);

            out.close();

            throw new FacesException(
                    "Can not write DefineRequested method content.", ex);
        }

        PrintWriter writer = new PrintWriter(outputStreamWriter);

        IJavaScriptWriter javaScriptWriter = new JavaScriptResponseWriter(
                servletContext, writer,
                ClientResourceBundleServlet.RESOURCE_BUNDLE_ENCODING);
        try {
            javaScriptWriter.writeCall("f_resourceBundle", "DefineLoaded");
            javaScriptWriter.writeString(baseName);
            javaScriptWriter.write(", [").writeln();

            int idx = 0;
            Enumeration en = resourceBundle.getKeys();
            for (; en.hasMoreElements(); idx++) {
                String key = (String) en.nextElement();
                String value = resourceBundle.getString(key);

                if (idx > 0) {
                    javaScriptWriter.write(',').writeln();
                }

                javaScriptWriter.write("\t");
                if (VERIFY_BUNDLE_KEY) {
                    verifyBundleKey(key);
                }
                javaScriptWriter.writeString(key);
                javaScriptWriter.write(',');
                if (value == null) {
                    javaScriptWriter.writeNull();
                    continue;
                }

                javaScriptWriter.writeString(value);
            }

            javaScriptWriter.write(" ]);");

            javaScriptWriter.end();

        } catch (WriterException ex) {
            throw new FacesException(
                    "Can not write DefineRequested method content.", ex);
        }

        writer.close();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Constucted method=" + writer.toString());
        }

        return out.toByteArray();
    }

    @SuppressWarnings("unused")
    private void verifyBundleKey(String key) {
        if (key.length() < 1) {
            throw new FacesException("Key of bundle can not be empty !");
        }

        if (false) {
            // On passe les clefs en "" plutot qu'en field !

            char cs[] = key.toCharArray();

            if (Character.isJavaIdentifierStart(cs[0]) == false) {
                throw new FacesException(
                        "Invalid key of bundle !  (first char is refused) (key='"
                                + key + "')");
            }

            for (int i = 1; i < cs.length; i++) {
                if (Character.isJavaIdentifierPart(cs[i]) == false) {
                    throw new FacesException("Invalid key of bundle ! (char #"
                            + (i + 1) + " is refused) (key='" + key + "')");
                }
            }
        }
    }

    static {
        if (VERIFY_BUNDLE_KEY) {
            LOG.info("VERIFY_BUNDLE_KEY=" + VERIFY_BUNDLE_KEY);
        }
    }
}
