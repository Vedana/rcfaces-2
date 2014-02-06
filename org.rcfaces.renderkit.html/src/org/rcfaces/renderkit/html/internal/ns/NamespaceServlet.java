/*
 * $Id: NamespaceServlet.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.core.internal.util.ConfigurationLoader;
import org.rcfaces.core.internal.util.ServletTools;
import org.rcfaces.core.internal.version.HashCodeTools;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.internal.webapp.ExpirationDate;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:02 $
 */
public class NamespaceServlet extends ConfiguredHttpServlet {

    private static final Log LOG = LogFactory.getLog(NamespaceServlet.class);

    private static final String SCHEMA_CHARSET = "UTF-8";

    private static final long serialVersionUID = 8060167303941387755L;

    private static final String RCFACES_NAMESPACE_PROPERTY_NAME = "rcfacesNS";

    private static final String DEFAULT_NAMESPACES_URL = "/rc-ns";

    private static final String SCHEMA_MIME_TYPE = "text/xml";

    private static final boolean PROCESS_IF_MODIFIED_SINCE = true;

    private ExpirationDate expirationDate;

    private final Object INIT_LOCK = new Object();

    private transient boolean initialized;

    private transient NamespaceConfigurationImpl namespaceConfiguration;

    private transient Map<String, INamespaceSchema> schemasByName = new HashMap<String, INamespaceSchema>();

    private String namespaceServletURL;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        namespaceServletURL = ServletTools.computeResourceURI(
                getServletContext(), DEFAULT_NAMESPACES_URL, getClass());
        if (namespaceServletURL == null) {
            return;
        }
        LOG.info("Base of application version url is '" + namespaceServletURL
                + "'.");

        Map accessor = createAccessor();

        config.getServletContext().setAttribute(
                RCFACES_NAMESPACE_PROPERTY_NAME, accessor);
    }

    @Override
    protected boolean hasLocaleSupport() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        synchronized (INIT_LOCK) {
            if (initialized == false) {
                initialized = true;

                namespaceConfiguration = loadConfig(request, response);

                initializeNamespaces(namespaceConfiguration);
            }
        }

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

        String ns = url.substring(0, idx);

        url = url.substring(idx + 1);

        idx = url.indexOf('/');
        if (idx < 0) {
            throw new ServletException("Can not understand URI '"
                    + request.getRequestURI() + "'.");
        }
        String version = url.substring(0, idx);
        String resourceName = url.substring(idx);

        INamespaceSchema namespaceSchema = schemasByName.get(ns);
        if (namespaceSchema == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Namespace not supported !");
            return;
        }

        BufferImpl buffer = (BufferImpl) namespaceSchema
                .getBuffer(resourceName);
        if (buffer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Buffer not found !");
            return;
        }

        if (buffer.getVersion().equals(version) == false) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Unknown version!");
            return;
        }

        buffer.send(request, response);
    }

    private void initializeNamespaces(
            NamespaceConfigurationImpl namespaceConfiguration2) {

        NS_Xhtml11 ns1 = new NS_Xhtml11();
        ns1.setNamespaceServlet(this, namespaceConfiguration);

        schemasByName.put(ns1.getName(), ns1);

    }

    private NamespaceConfigurationImpl loadConfig(HttpServletRequest request,
            HttpServletResponse response) {

        final NamespaceConfigurationImpl[] ref = new NamespaceConfigurationImpl[1];

        RcfacesContext.runIntoFacesContext(getServletContext(), request,
                response, new Runnable() {

                    public void run() {
                        FacesContext facesContext = FacesContext
                                .getCurrentInstance();

                        ref[0] = loadConfig(facesContext, getServletContext());
                    }

                });

        return ref[0];
    }

    private NamespaceConfigurationImpl loadConfig(FacesContext facesContext,
            Object classLoaderFallback) {

        List urls = new ArrayList();
        ConfigurationLoader.scanFacesConfig(facesContext.getExternalContext(),
                urls);

        ConfigurationLoader configurationLoader = ConfigurationLoader
                .scanRCFacesConfig(facesContext.getExternalContext(), urls);

        Set<String> classes = new HashSet<String>();

        Digester digester = constructDigester(classes);

        configurationLoader.parse(digester);

        NamespaceConfigurationImpl configuration = new NamespaceConfigurationImpl();

        for (String className : classes) {

            processClass(facesContext, classLoaderFallback, configuration,
                    className);
        }

        return configuration;
    }

    private void processClass(FacesContext facesContext,
            Object classLoaderFallback, INamespaceConfiguration configuration,
            String className) {

        Class< ? > clazz;
        try {
            clazz = ClassLocator.load(className, classLoaderFallback,
                    facesContext);

        } catch (Exception ex) {
            LOG.error("Can not process class '" + className + "'.", ex);
            return;
        }

        if (clazz.isAssignableFrom(INamespaceContributor.class) == false) {
            return;
        }

        try {
            processObject((Class<INamespaceContributor>) clazz, configuration);

        } catch (Exception ex) {
            LOG.error("Can not process object of class '" + className + "'.",
                    ex);
            return;
        }
    }

    private void processObject(Class<INamespaceContributor> clazz,
            INamespaceConfiguration configuration)
            throws InstantiationException, IllegalAccessException {

        INamespaceContributor contributor = clazz.newInstance();

        contributor.declare(configuration);
    }

    private Digester constructDigester(final Set<String> classesName) {
        Digester digester = new Digester();

        digester.addRule("faces-config/render-kit/renderer/renderer-class",
                new Rule() {

                    @Override
                    public void body(String namespace, String name, String text)
                            throws Exception {
                        if (text != null) {
                            text = text.trim();
                            if (text.length() > 0) {
                                classesName.add(text);
                            }
                        }
                    }
                });

        digester.addRule(
                "rcfaces-config/borders/render-kit/border-renderer/renderer-class",
                new Rule() {

                    @Override
                    public void body(String namespace, String name, String text)
                            throws Exception {
                        if (text != null) {
                            text = text.trim();
                            if (text.length() > 0) {
                                classesName.add(text);
                            }
                        }
                    }
                });

        return digester;
    }

    private Map<String, INamespaceSchema> createAccessor() {
        return new HashMap<String, INamespaceSchema>() {
            private static final long serialVersionUID = 594142032443254472L;

            public INamespaceSchema get(Object key) {
                INamespaceSchema namespaceSchemas = schemasByName.get(key);
                return namespaceSchemas;
            }
        };
    }

    public IBuffer recordBuffer(INamespaceSchema namespaceSchema,
            String bufferName, String bufferContent) {

        String versionURL = HashCodeTools.computeURLFormat(null, bufferName,
                bufferContent, Constants.VERSIONED_URI_HASHCODE_MAX_SIZE);

        StringAppender sa = new StringAppender(1024);
        sa.append(namespaceServletURL);
        sa.append('/');
        sa.append(versionURL);
        sa.append('/');
        sa.append(bufferName);

        return new BufferImpl(bufferName, sa.toString(), bufferContent,
                versionURL);
    }

    public interface IBuffer {

        String getLocation();
    }

    protected class BufferImpl implements IBuffer {

        private final String name;

        private final String etag;

        private final String hash;

        private final String location;

        private final byte[] buffer;

        private final String version;

        public BufferImpl(String name, String location, String content,
                String version) {
            this.name = name;
            this.location = location;
            this.version = version;

            try {
                this.buffer = content.getBytes(SCHEMA_CHARSET);

            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }

            if (hasEtagSupport()) {
                etag = computeETag(buffer);
            } else {
                etag = null;
            }

            if (hasHashSupport()) {
                hash = computeHash(buffer);

            } else {
                hash = null;
            }

        }

        public String getVersion() {
            return version;
        }

        public String getLocation() {
            return location;
        }

        public void send(HttpServletRequest request,
                HttpServletResponse response) throws IOException {

            if (etag != null) {
                String ifETag = request.getHeader(HTTP_IF_NONE_MATCH);
                if (ifETag != null) {
                    if (etag.equals(ifETag)) {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }
            }

            if (hash != null) {
                String isHash = request.getHeader(HTTP_IF_NOT_HASH);
                if (isHash != null) {
                    if (hash.equals(isHash)) {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }
            }

            if (PROCESS_IF_MODIFIED_SINCE) {
                long ifModifiedSince = request
                        .getDateHeader(HTTP_IF_MODIFIED_SINCE);
                if (ifModifiedSince > 0) {
                    ifModifiedSince -= (ifModifiedSince % 1000);

                    if (true) {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }
            }

            response.setContentType(SCHEMA_MIME_TYPE + "; charset="
                    + SCHEMA_CHARSET);

            if (etag != null) {
                response.setHeader(HTTP_ETAG, etag);
            }

            if (hash != null) {
                response.setHeader(HTTP_HASH, hash);
            }

            response.setContentLength(buffer.length);

            if (expirationDate != null) {
                expirationDate.sendExpires(response);
            }

            response.getOutputStream().write(buffer);
        }
    }
}
