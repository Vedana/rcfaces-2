/*
 * $Id: StylesheetsServlet.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */

package org.rcfaces.renderkit.html.internal.css;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.faces.FacesException;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Globals;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.codec.SourceFilter;
import org.rcfaces.core.internal.lang.ByteBufferOutputStream;
import org.rcfaces.core.internal.renderkit.AbstractRendererTypeFactory;
import org.rcfaces.core.internal.repository.SourceContainer.IParameterizedContent;
import org.rcfaces.core.internal.util.ServletTools;
import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;
import org.rcfaces.core.internal.webapp.ExpirationDate;
import org.rcfaces.core.internal.webapp.URIParameters;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.HtmlModulesServlet;
import org.rcfaces.renderkit.html.internal.IHtmlProcessContext;
import org.rcfaces.renderkit.html.internal.agent.ClientBrowserFactory;
import org.rcfaces.renderkit.html.internal.agent.IClientBrowser;
import org.rcfaces.renderkit.html.internal.agent.IUserAgent;
import org.rcfaces.renderkit.html.internal.agent.IUserAgentRules;
import org.rcfaces.renderkit.html.internal.agent.UserAgentRuleTools;
import org.rcfaces.renderkit.html.internal.renderer.HtmlRendererTypeFactory;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
public class StylesheetsServlet extends HtmlModulesServlet {

    private static final long serialVersionUID = 708578720264413327L;

    private static final Log LOG = LogFactory.getLog(StylesheetsServlet.class);

    // private static final String BASE_DIRECTORY =
    // StylesheetsServlet.class.getPackage().getName().replace('.', '/') + '/';

    static final String CAMELIA_CSS_URL = "rcfaces.css";

    static final String CAMELIA_CSS_PREFIX = "rcfaces";

    static final String CAMELIA_CSS_SUFFIX = ".css";

    private final int MAX_404_RESPONSE = 64;

    private static final String CSS_CONFIG_PROPERTY = "org.rcfaces.renderkit.html.CSS_CONFIG";

    private static Map<String, String> extensions = new HashMap<String, String>(
            10);
    {
        extensions.put("js", JAVASCRIPT_MIME_TYPE);
        extensions.put("txt", TEXT_PLAIN_MIME_TYPE);
        extensions.put("css", CSS_MIME_TYPE);
        extensions.put("html", HTML_MIME_TYPE);
        extensions.put("gif", "image/gif");
        extensions.put("jpg", "image/jpeg");
        extensions.put("jpeg", "image/jpg");
        extensions.put("png", "image/png");
        extensions.put("cur", "image/x-win-bitmap");
    }

    private static Set<String> useFilterExtensions = new HashSet<String>();
    static {
        useFilterExtensions.add(CSS_MIME_TYPE);
    }

    private static Set useFilterSkipSpacesExtensions = new HashSet();
    static {
        // useFilterSkipSpacesExtensions.add("css");
    }

    private static final String REPOSITORY_VERSION_SUPPORT_PARAMETER = Constants
            .getPackagePrefix() + ".REPOSITORY_VERSION_SUPPORT";

    private static final String CONFIGURATION_VERSION_PARAMETER = Constants
            .getPackagePrefix() + ".CONFIGURATION_VERSION";

    private static final String NO_CACHE_PARAMETER = Constants
            .getPackagePrefix() + ".NO_CACHE";

    private static final int WORK_BUFFER_SIZE = 32000;

    private static final String CHARSET_PARAMETER = Constants
            .getPackagePrefix() + ".CSS_CHARSET";

    private static final String DEFAULT_MODULE_NAME = "core";

    private final Map<String, Object> bufferedResponse = new HashMap<String, Object>(
            1024);

    private String styleSheetURI;

    private int count404Responses;

    private int count200Responses;

    private boolean noCache = false;

    private final Map<String, ModuleRepository> moduleRepositories = new HashMap<String, ModuleRepository>(
            8);

    // private Object useMetaContentStyleType;

    // private String repositoryVersion;

    private String charset;

    private String renderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;

    public StylesheetsServlet() {
    }

    public StylesheetsServlet(String styleSheetURI) {
        this();
        this.styleSheetURI = styleSheetURI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {

        if (styleSheetURI == null) {
            styleSheetURI = ServletTools.computeResourceURI(
                    config.getServletContext(), null, getClass());
        }

        super.init(config);

        String nc = getParameter(NO_CACHE_PARAMETER);
        if ("true".equalsIgnoreCase(nc)) {
            noCache = true;
        }

        charset = getParameter(CHARSET_PARAMETER);
        if (charset != null) {
            LOG.info("Charset setted to \"" + charset + "\"  for sevlet '"
                    + getServletName() + "'.");
        } else {
            charset = Constants.CSS_DEFAULT_CHARSET;

            LOG.info("Charset setted to DEFAULT value: \"" + charset
                    + "\"  for sevlet '" + getServletName() + "'");
        }

        String repositoriesPaths[] = RcfacesContext
                .getInstance(getServletContext(), null, null)
                .getRepositoryManager().listRepositoryLocations("css");

        for (int i = 0; i < repositoriesPaths.length; i++) {
            loadRepository(repositoriesPaths[i]);
        }
    }

    @SuppressWarnings("unused")
    private void loadRepository(String repositoryPath) throws ServletException {

        final Set<String> modules = new HashSet<String>();

        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);

        digester.setEntityResolver(new EntityResolver() {

            public InputSource resolveEntity(String string, String string1) {
                return new InputSource(new CharArrayReader(new char[0]));
            }

        });

        final String[] baseDirectoryRef = new String[1];

        final Map<String, Set<IUserAgentRules>> agentRulesAgentVaryByModule = new HashMap<String, Set<IUserAgentRules>>();

        digester.addRule("repository", new Rule() {

            public void begin(String namespace, String name,
                    Attributes attributes) {

                String baseDirectory = attributes.getValue("baseDirectory");

                if (baseDirectory != null && baseDirectory.length() > 0) {
                    baseDirectoryRef[0] = baseDirectory;
                }
            }
        });

        digester.addRule("repository/module", new Rule() {

            private String currentModuleName;

            public void begin(String namespace, String name,
                    Attributes attributes) {

                String id = attributes.getValue("id");

                if (id != null) {
                    modules.add(id);

                    currentModuleName = id;
                    getDigester().push(currentModuleName);
                }
            }

            public void end(String namespace, String name) throws Exception {
                super.end(namespace, name);

                if (currentModuleName != null) {
                    currentModuleName = null;

                    getDigester().pop();
                }
            }

        });

        RcfacesContext rcfacesContext = RcfacesContext.getInstance(
                getServletContext(), null, null);

        final HtmlRendererTypeFactory htmlRendererTypeFactory = (HtmlRendererTypeFactory) AbstractRendererTypeFactory
                .get(rcfacesContext, renderKitId);

        final Map<String, IUserAgentRules> userAgentRuleList = htmlRendererTypeFactory
                .listFeaturesByNames();

        digester.addRule("repository/module/file", new Rule() {

            public void begin(String namespace, String name,
                    Attributes attributes) {

                String userAgent = attributes.getValue("userAgent");
                if (userAgent != null) {
                    String currentModuleName = (String) getDigester().peek();

                    IUserAgentRules userAgentRules = UserAgentRuleTools
                            .constructUserAgentRules(userAgent, false, true, 0,
                                    userAgentRuleList);

                    Set<IUserAgentRules> uar = agentRulesAgentVaryByModule
                            .get(currentModuleName);
                    if (uar == null) {
                        uar = new HashSet<IUserAgentRules>();
                        agentRulesAgentVaryByModule.put(currentModuleName, uar);
                    }

                    uar.add(userAgentRules);
                }
            }
        });

        URL url = getClass().getClassLoader().getResource(repositoryPath);
        if (url == null) {
            LOG.error("Can not get URL for path '" + repositoryPath + "'.",
                    null);
            return;
        }

        URLConnection urlConnection;
        try {
            urlConnection = url.openConnection();

        } catch (IOException ex) {
            LOG.error("Can not get content of '" + repositoryPath + "'.", ex);

            return;
        }

        long lastModified = urlConnection.getLastModified();

        InputStream ins;
        try {
            ins = urlConnection.getInputStream();

        } catch (IOException ex) {
            LOG.error("Can not get content of '" + repositoryPath + "'.", ex);

            return;
        }

        try {
            digester.parse(ins);

        } catch (Exception ex) {
            LOG.error("Can not parse XML file '" + repositoryPath + "'", ex);
            return;

        } finally {
            try {
                ins.close();

            } catch (Exception ex) {
                LOG.error("Can not close stream", ex);
            }
        }

        String baseURL = baseDirectoryRef[0];

        for (Iterator it = modules.iterator(); it.hasNext();) {
            String moduleName = (String) it.next();

            if (Constants.RESOURCES_MODULE_URL_SUPPORT == false) {
                if (DEFAULT_MODULE_NAME.equals(moduleName) == false) {
                    throw new ServletException(
                            "RESOURCES_MODULE_URL_SUPPORT must be enabled if the module name is not '"
                                    + DEFAULT_MODULE_NAME + "'.");
                }
            }

            if (moduleRepositories.containsKey(moduleName)) {
                // throw new
                // ServletException("Several modules defined in different files (name='"
                // + moduleName + "')");
                it.remove();
                continue;
            }
        }

        if (modules.isEmpty()) {
            return;
        }

        String repositoryVersionSupport = getParameter(REPOSITORY_VERSION_SUPPORT_PARAMETER);

        for (Iterator it = modules.iterator(); it.hasNext();) {

            String moduleName = (String) it.next();
            StyleSheetSourceContainer styleSheetSourceContainer = createStyleSheetRepository(moduleName);
            String configurationVersion = null;
            if ("false".equalsIgnoreCase(repositoryVersionSupport) == false) {
                configurationVersion = getParameter(CONFIGURATION_VERSION_PARAMETER
                        + "." + moduleName);
                if (configurationVersion == null) {
                    configurationVersion = getParameter(CONFIGURATION_VERSION_PARAMETER);
                }
                if (configurationVersion != null
                        && configurationVersion.length() == 0) {
                    throw new ServletException(
                            "Context-Param : org.rcfaces.renderkit.html.CONFIGURATION_VERSION can not be an empty string");
                }

                if (configurationVersion == null) {
                    if (DEFAULT_MODULE_NAME.equals(moduleName)) {
                        configurationVersion = Constants.getBuildId();
                    }
                }

                if (configurationVersion == null && lastModified > 0) {
                    configurationVersion = "." + lastModified;
                }

                LOG.info("Set module '" + moduleName + "' version to '"
                        + configurationVersion + "' for servlet '"
                        + getServletName() + "'.");
            }

            ModuleRepository moduleRepository = new ModuleRepository(
                    styleSheetSourceContainer, baseURL, configurationVersion,
                    moduleName);//

            moduleRepositories.put(moduleName, moduleRepository);

            String propertyName = CSS_CONFIG_PROPERTY + ":" + moduleName;

            if (getServletContext().getAttribute(propertyName) == null) {
                String fileName = CAMELIA_CSS_URL;
                String version = moduleRepository.getVersion();
                if (version != null) {
                    URIParameters up = URIParameters.parseURI(fileName);

                    // up.appendVersion(version);

                    fileName = up.computeParametredURI();
                }

                String uri = styleSheetURI;

                if (Constants.RESOURCES_MODULE_URL_SUPPORT) {
                    if (moduleName != null) {
                        uri += "/" + moduleName;
                    }
                }

                if (Constants.VERSIONED_FRAMEWORK_URL_SUPPORT) {
                    if (version != null) {
                        uri += "/" + version;
                    }
                }

                Set<IUserAgentRules> agentRulesAgentVary = agentRulesAgentVaryByModule
                        .get(moduleName);

                getServletContext().setAttribute(
                        propertyName,
                        new CssConfig(fileName, uri, styleSheetSourceContainer
                                .getCharSet(), agentRulesAgentVary));
            }
            if (noCache == false) {
                styleSheetSourceContainer.getDefaultContent().getRawBuffer();
            }
        }

    }

    protected final String getCharset() {
        return charset;
    }

    public static ICssConfig getConfig(IHtmlProcessContext htmlExternalContext) {
        return getConfig(htmlExternalContext, DEFAULT_MODULE_NAME);
    }

    public static ICssConfig getConfig(IHtmlProcessContext htmlExternalContext,
            String moduleName) {
        ICssConfig cssConfig = (ICssConfig) htmlExternalContext
                .getFacesContext().getExternalContext().getApplicationMap()
                .get(CSS_CONFIG_PROPERTY + ":" + moduleName);

        if (cssConfig == null) {
            throw new FacesException(
                    "No initialized config for stylesheet module '"
                            + moduleName
                            + "' ! (You have forgotten the RCFaces servlet, or its startup has failed !)");
        }

        return cssConfig;
    }

    private StyleSheetSourceContainer getRepository(String moduleName)
            throws ServletException {

        ModuleRepository mr = moduleRepositories.get(moduleName);
        if (mr == null) {
            throw new ServletException("Invalid module '" + moduleName + "'.");
        }

        if (noCache) {
            LOG.debug("'noCache' is enable, ignore new repository !");

            return createStyleSheetRepository(mr.getModule());
        }

        return mr.getStyleSheetRepository();
    }

    private StyleSheetSourceContainer createStyleSheetRepository(String module)
            throws ServletException {

        /* Pas de version au niveau du container ! */

        return new StyleSheetSourceContainer(getServletConfig(), module,
                getCharset(), hasGZipSupport(), hasEtagSupport(),
                hasHashSupport(), null, renderKitId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String requestURL = (String) request
                .getAttribute(Globals.INCLUDE_REQUEST_URI_ATTR);
        if (requestURL == null) {
            requestURL = request.getRequestURI();
        }

        String url = requestURL;

        String contextPath = (String) request
                .getAttribute(Globals.INCLUDE_CONTEXT_PATH_ATTR);
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }

        if (contextPath != null) {
            url = url.substring(contextPath.length());
        }

        String servletPath = (String) request
                .getAttribute(Globals.INCLUDE_SERVLET_PATH_ATTR);
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("New request uri='" + requestURL + "' contextPath='"
                    + contextPath + "' servletPath='" + servletPath + "'.");
        }

        if (servletPath != null) {
            url = url.substring(servletPath.length());
        }

        int idx = url.indexOf('/');
        if (idx >= 0) {
            url = url.substring(idx + 1);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Filtred url='" + url + "' for requestURI='" + requestURL
                    + "'.");
        }

        String moduleName = DEFAULT_MODULE_NAME;

        if (Constants.RESOURCES_MODULE_URL_SUPPORT) {
            idx = url.indexOf('/');
            if (idx < 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            moduleName = url.substring(0, idx);
            url = url.substring(idx + 1);
        }

        ModuleRepository moduleRepository = moduleRepositories.get(moduleName);

        if (moduleRepository == null) {
            LOG.error("Unknown module ! (moduleName='" + moduleName + "')");

            setNoCache(response);
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Unknown module '" + moduleName + "'");
            return;
        }

        boolean versionned = false;

        if (moduleRepository.getVersion() != null) {
            String version = null;
            if (Constants.VERSIONED_FRAMEWORK_URL_SUPPORT) {
                idx = url.indexOf('/');
                if (idx < 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                version = url.substring(0, idx);
                url = url.substring(idx + 1);

            } else {
                URIParameters up = URIParameters.parseURI(url);
                if (up != null) {
                    version = up.getVersion();
                    url = up.getURI();
                }
            }

            if (version != null) {
                versionned = true;

                String repositoryVersion = moduleRepository.getVersion();
                if (repositoryVersion != null) {
                    if (repositoryVersion.equals(version) == false) {
                        LOG.error("Not same repository version ! (current="
                                + repositoryVersion + " request=" + version
                                + ")");

                        setNoCache(response);
                        response.sendError(HttpServletResponse.SC_CONFLICT,
                                "Invalid RCFaces version !");
                        return;
                    }
                }
            }
        }

        String resourceKey = moduleName + "/" + url;

        Response res = null;
        ResponseFacade responseFacade = null;
        synchronized (bufferedResponse) {
            Object ret = bufferedResponse.get(resourceKey);
            if (ret == null) {
                responseFacade = new ResponseFacade(url, moduleRepository);
                if (noCache == false) {
                    bufferedResponse.put(resourceKey, responseFacade);
                }

            } else if (ret instanceof Response) {
                res = (Response) ret;

            } else {
                responseFacade = (ResponseFacade) ret;
            }
        }

        if (res != null) {
            res.send(request, response);
            return;
        }

        // Ca ne peut être NULL !
        if (responseFacade != null) {
            responseFacade.send(request, response, versionned);
        }
    }

    private Response record200(String url, InputStream in, int size,
            long lastModified, boolean versionned) throws IOException,
            ServletException {

        int ex = url.lastIndexOf('.');
        int ex2 = url.lastIndexOf('/');
        if (ex < 0 || ex < ex2) {
            throw new ServletException("Can not find extension of url '" + url
                    + "'");
        }
        String extension = url.substring(ex + 1).toLowerCase();

        String mimeType = extensions.get(extension);
        if (mimeType == null) {
            throw new ServletException("Unknown extension '" + extension + "'");
        }

        if (size < 1) {
            size = WORK_BUFFER_SIZE;
        }

        ByteBufferOutputStream bout = new ByteBufferOutputStream(size);

        byte w[] = new byte[8000];
        for (;;) {
            int ret = in.read(w);

            if (ret <= 0) {
                break;
            }

            bout.write(w, 0, ret);
        }

        byte workBytes[] = bout.toByteArray();

        bout.close();

        if (charset == null) {
            charset = getCharset();
        }

        if (useFilterExtensions.contains(extension)) {
            String filtred = SourceFilter
                    .filter(new String(workBytes, charset));
            workBytes = filtred.getBytes(charset);

        } else if (useFilterSkipSpacesExtensions.contains(extension)) {
            String filtred = SourceFilter.filterSkipSpaces(new String(
                    workBytes, charset));
            workBytes = filtred.getBytes(charset);
        }

        byte bufferGZIP[] = null;
        if (hasGZipSupport() && isMimeTypeSupportGZip(mimeType)) {
            try {
                ByteBufferOutputStream bos = new ByteBufferOutputStream(
                        workBytes.length);
                GZIPOutputStream gzos = new GZIPOutputStream(bos,
                        workBytes.length);
                gzos.write(workBytes);
                gzos.close();

                bos.close();

                bufferGZIP = bos.toByteArray();

            } catch (IOException ex3) {
                throw new ServletException(
                        "Can not create GZIP buffer of adonis css files.", ex3);
            }
        }

        Response res = new BytesResponse(workBytes, mimeType, bufferGZIP,
                lastModified, versionned);

        return res;
    }

    private Response record404(String url) {

        return new NotFoundResponse(url);
        /*
         * if (count404Responses < MAX_404_RESPONSE) { count404Responses++;
         * 
         * synchronized (bufferedResponse) { bufferedResponse.put(url, new
         * NotFoundResponse(url)); } }
         * 
         * response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL requested
         * not found '" + url + "'");
         */
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    private class ResponseFacade {

        private final String url;

        private final ModuleRepository moduleRepository;

        private Response response;

        public ResponseFacade(String url, ModuleRepository moduleRepository) {
            this.url = url;
            this.moduleRepository = moduleRepository;
        }

        public synchronized void send(HttpServletRequest httpRequest,
                HttpServletResponse httpResponse, boolean versionned)
                throws IOException, ServletException {
            if (url.indexOf("..") >= 0 || url.indexOf("//") >= 0
                    || url.indexOf("\\") >= 0) {
                throw new ServletException("Invalid url '" + url + "'");
            }

            if (url.startsWith(CAMELIA_CSS_PREFIX)
                    && url.endsWith(CAMELIA_CSS_SUFFIX)) {
                if (url.indexOf('/') >= 0) {
                    throw new ServletException("Invalid url '" + url + "'");
                }

                boolean cacheResponse = true;

                IClientBrowser clientBrowser = null;

                IClientBrowser userAgentClientBrowser = ClientBrowserFactory
                        .Get().get(httpRequest);

                IClientBrowser urlAgentClientBrowser = null;

                URIParameters uriParameters = URIParameters.parseURI(url);
                if (uriParameters != null && uriParameters.getAgent() != null) {

                    String urlAgent = uriParameters.getAgent();
                    // Un parametre dans l'URL
                    urlAgentClientBrowser = ClientBrowserFactory.Get()
                            .getClientBrowserById(urlAgent);

                    if (userAgentClientBrowser != null) {
                        if (userAgentClientBrowser
                                .equalsType(urlAgentClientBrowser) == false) {

                            // L'agent utilisé n'est pas le même
                            cacheResponse = false;

                            // Pas de cache ... car c'est pas normal !
                            setNoCache(httpResponse);

                            // Mais on prend le clientBrowser du BROWSER
                            clientBrowser = userAgentClientBrowser;

                        } else {
                            // L'agent demandé est celui du browser => RAS

                            clientBrowser = userAgentClientBrowser;
                        }
                    } else {
                        // Pas d'agent dans l'URL
                    }

                } else {
                    // Pas de parametre dans l'URL .... on desactive le
                    // clientBrowser
                    userAgentClientBrowser = null;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("userAgentClientBrowser='"
                            + userAgentClientBrowser
                            + "' urlAgentClientBrowser='"
                            + urlAgentClientBrowser + "' => " + clientBrowser);
                }

                StyleSheetRepositoryResponse repositoryResponse = new StyleSheetRepositoryResponse(
                        CSS_MIME_TYPE + "; charset=" + getCharset(),
                        moduleRepository, clientBrowser);

                setResponse(repositoryResponse, httpRequest, httpResponse,
                        cacheResponse);
                return;
            }

            if (response != null) {
                response.send(httpRequest, httpResponse);
                return;
            }

            URL resourceURL = getClass().getClassLoader().getResource(
                    moduleRepository.getBaseURL() + url);
            if (resourceURL == null) {
                setResponse(record404(url), httpRequest, httpResponse, true);
                return;
            }

            URLConnection urlConnection = resourceURL.openConnection();
            if (urlConnection == null) {
                setResponse(record404(url), httpRequest, httpResponse, true);
                return;
            }

            long lastModified = urlConnection.getLastModified();
            int size = urlConnection.getContentLength();

            InputStream in = urlConnection.getInputStream();
            if (in == null) {
                setResponse(record404(url), httpRequest, httpResponse, true);
                return;
            }

            try {
                setResponse(record200(url, in, size, lastModified, versionned),
                        httpRequest, httpResponse, true);
                return;

            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    log("Can not close pipe '" + in + "'", ex);
                }
            }
        }

        private void setResponse(Response response,
                HttpServletRequest httpRequest,
                HttpServletResponse httpResponse, boolean cacheResponse)
                throws IOException, ServletException {
            this.response = response;

            if (noCache == false && cacheResponse) {
                synchronized (bufferedResponse) {
                    bufferedResponse.put(url, response);
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Response for '" + url + "' => " + response);
            }

            response.send(httpRequest, httpResponse);
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    private interface Response {

        public void send(HttpServletRequest request,
                HttpServletResponse response) throws IOException,
                ServletException;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    private class NotFoundResponse implements Response {
        private final String message;

        public NotFoundResponse(String url) {
            message = "URL requested not found '" + url + "'";
        }

        public void send(HttpServletRequest request,
                HttpServletResponse response) throws IOException {

            if (Constants.STAT_RESOURCES_HTTP_RESPONSE) {
                synchronized (StylesheetsServlet.this) {
                    count404Responses++;
                }
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    private class BytesResponse extends AbstractResponse {

        private final byte buffer[];

        private final long lastModified;

        private final byte bufferGZIP[];

        private final String etag;

        private final String hash;

        private final boolean versionned;

        public BytesResponse(byte[] buffer, String mimeType, byte bufferGZIP[],
                long lastModified, boolean versionned) {
            super(mimeType);

            this.buffer = buffer;
            this.bufferGZIP = bufferGZIP;
            this.versionned = versionned;

            this.lastModified = lastModified;

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

        protected boolean isVersionned() {
            return versionned;
        }

        protected byte[] getBuffer() {
            return buffer;
        }

        protected byte[] getGZipedBuffer() {
            return bufferGZIP;
        }

        protected long getLastModified() {
            return lastModified;
        }

        protected String getETag() {
            return etag;
        }

        protected String getHash() {
            return hash;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    private abstract class AbstractResponse implements Response {

        private final String mimeType;

        private String charSet;

        public AbstractResponse(String mimeType) {
            this.mimeType = mimeType;
        }

        protected boolean isVersionned() {
            return false;
        }

        public final void send(HttpServletRequest request,
                HttpServletResponse response) throws IOException,
                ServletException {

            long lastModified = getLastModified();
            if (lastModified > 0) {
                lastModified -= (lastModified % 1000);
            }

            if (noCache) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Set no cache for response.");
                }
                ConfiguredHttpServlet.setNoCache(response);

            } else {
                ExpirationDate expirationDate = getDefaultExpirationDate(isVersionned());
                if (expirationDate != null) {
                    expirationDate.sendExpires(response);
                }

                if (lastModified > 0) {
                    response.setDateHeader(HTTP_LAST_MODIFIED, lastModified);
                }
            }

            String etag = getETag();
            if (etag != null) {
                String ifETag = request.getHeader(HTTP_IF_NONE_MATCH);
                if (etag.equals(ifETag)) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }

            String hash = getHash();
            if (hash != null) {
                String ifHash = request.getHeader(HTTP_IF_NOT_HASH);
                if (hash.equals(ifHash)) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }

            if (lastModified > 0) {
                long ifModifiedSince = request
                        .getDateHeader(HTTP_IF_MODIFIED_SINCE);
                if (ifModifiedSince > 0) {
                    ifModifiedSince -= (ifModifiedSince % 1000);

                    if (ifModifiedSince >= lastModified) {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }
            }

            byte buf[] = getBuffer();

            response.setContentType(mimeType);

            byte gzip[] = getGZipedBuffer();
            if (gzip != null) {
                if (hasGzipSupport(request)) {

                    setGzipContentEncoding(response, false);

                    buf = gzip;
                }
            }

            setVaryAcceptEncoding(response);

            response.setContentLength(buf.length);
            if (etag != null) {
                response.setHeader(HTTP_ETAG, etag);
            }
            if (hash != null) {
                response.setHeader(HTTP_HASH, hash);
            }

            if (Constants.STAT_RESOURCES_HTTP_RESPONSE) {
                synchronized (StylesheetsServlet.this) {
                    count200Responses++;
                }
            }

            OutputStream out = response.getOutputStream();
            out.write(buf);
        }

        protected abstract byte[] getBuffer() throws ServletException;

        protected abstract byte[] getGZipedBuffer() throws ServletException;

        protected abstract long getLastModified() throws ServletException;

        protected abstract String getETag() throws ServletException;

        protected abstract String getHash() throws ServletException;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    private class StyleSheetRepositoryResponse extends AbstractResponse {

        private final StyleSheetSourceContainer styleSheetRepository;

        private final IParameterizedContent parameterizedContent;

        private final ModuleRepository moduleRepository;

        public StyleSheetRepositoryResponse(String mimeType,
                ModuleRepository moduleRepository, IClientBrowser clientBrowser)
                throws ServletException {
            super(mimeType);

            this.moduleRepository = moduleRepository;
            styleSheetRepository = moduleRepository.getStyleSheetRepository();

            if (clientBrowser != null) {
                parameterizedContent = styleSheetRepository
                        .getContent(clientBrowser);

            } else {
                parameterizedContent = styleSheetRepository.getDefaultContent();
            }
        }

        protected boolean isVersionned() {
            return moduleRepository.getVersion() != null;
        }

        protected byte[] getBuffer() throws ServletException {
            return parameterizedContent.getRawBuffer();
        }

        protected byte[] getGZipedBuffer() {
            return parameterizedContent.getGZipedBuffer();
        }

        protected long getLastModified() {
            return parameterizedContent.getLastModified();
        }

        protected String getETag() {
            return parameterizedContent.getETag();
        }

        protected String getHash() {
            return parameterizedContent.getHash();
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    protected static final class CssConfig implements ICssConfig {

        private final Map<String, String> userAgentVariesFileName;

        private final String styleSheetURI;

        private final String styleSheetFileName;

        private final String charSet;

        private final Collection<IUserAgentRules> agentRulesAgentVary;

        public CssConfig(String styleSheetFileName, String styleSheetURI,
                String charSet, Set<IUserAgentRules> agentRulesAgentVary) {
            this.styleSheetFileName = styleSheetFileName;
            this.styleSheetURI = styleSheetURI;
            this.charSet = charSet;
            this.agentRulesAgentVary = agentRulesAgentVary;

            this.userAgentVariesFileName = (agentRulesAgentVary != null && agentRulesAgentVary
                    .size() > 0) ? (new HashMap<String, String>()) : null;
        }

        public String getDefaultStyleSheetURI() {
            return styleSheetURI;
        }

        public String getStyleSheetFileName(IClientBrowser clientBrowser) {
            if (userAgentVariesFileName == null) {
                return styleSheetFileName;
            }

            String browserIdAndVersion = clientBrowser.getBrowserIdAndVersion();

            synchronized (userAgentVariesFileName) {
                String uri = userAgentVariesFileName.get(browserIdAndVersion);
                if (uri != null) {
                    return uri;
                }

                IUserAgent target = clientBrowser;
                for (IUserAgentRules ua : agentRulesAgentVary) {
                    IUserAgent r = ua.reduce(target);
                    if (r == null) {
                        // Notre clientBrowser non impacté par cette regle
                        continue;
                    }
                    // Version impactée par cette regle
                    target = r;
                }
                if (target != clientBrowser) {
                    URIParameters uriParameters = URIParameters
                            .parseURI(styleSheetFileName);
                    uriParameters.appendAgent(browserIdAndVersion);

                    uri = uriParameters.computeParametredURI();

                } else {
                    uri = styleSheetFileName;
                }

                userAgentVariesFileName.put(browserIdAndVersion, uri);

                return uri;
            }
        }

        public String getCharSet() {
            return charSet;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    protected static class ModuleRepository {
        private final StyleSheetSourceContainer styleSheetSourceContainer;

        private final String baseURL;

        private final String module;

        private final String version;

        public ModuleRepository(
                StyleSheetSourceContainer styleSheetSourceContainer,
                String baseURL, String version, String module) {
            this.version = version;
            this.styleSheetSourceContainer = styleSheetSourceContainer;

            if (baseURL.endsWith("/") == false) {
                baseURL += "/";
            }

            this.baseURL = baseURL;
            this.module = module;
        }

        public String getVersion() {
            return version;
        }

        public String getModule() {
            return module;
        }

        public StyleSheetSourceContainer getStyleSheetRepository() {
            return styleSheetSourceContainer;
        }

        public String getBaseURL() {
            return baseURL;
        }
    }
}