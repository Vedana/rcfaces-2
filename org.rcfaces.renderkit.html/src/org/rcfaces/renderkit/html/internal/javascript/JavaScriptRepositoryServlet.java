/*
 * $Id: JavaScriptRepositoryServlet.java,v 1.4 2013/11/13 12:53:32 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.lang.ByteBufferInputStream;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.repository.HierarchicalRepositoryServlet;
import org.rcfaces.core.internal.repository.IContentRef;
import org.rcfaces.core.internal.repository.IHierarchicalRepository;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.IHierarchicalFile;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.IModule;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.ISet;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.IRepository.IContent;
import org.rcfaces.core.internal.repository.IRepository.IContext;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.repository.URLContentRef;
import org.rcfaces.core.internal.tools.ContextTools;
import org.rcfaces.core.internal.util.ApplicationParametersMap;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.core.internal.util.ServletTools;
import org.rcfaces.core.internal.webapp.ExpirationDate;
import org.rcfaces.renderkit.html.internal.Constants;
import org.rcfaces.renderkit.html.internal.IHtmlProcessContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.agent.ClientBrowserFactory;
import org.rcfaces.renderkit.html.internal.agent.IUserAgent;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository.IClass;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository.IClassFile;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository.ISymbolFile;
import org.rcfaces.renderkit.html.internal.util.UserAgentCriteria;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
 */
public class JavaScriptRepositoryServlet extends HierarchicalRepositoryServlet {

    private static final long serialVersionUID = -2654621696260702001L;

    private static final Log LOG = LogFactory
            .getLog(JavaScriptRepositoryServlet.class);

    private static final String CLEAR_VARIABLES[] = { "__statics", "__members",
            "__resources", "__prototype" };

    private static final String MAIN_REPOSITORY_DIRECTORY_LOCATION = JavaScriptRepository.class
            .getPackage().getName().replace('.', '/');

    // private static final String MAIN_REPOSITORY_LOCATION = "/repository.xml";

    private static final String PARAMETER_PREFIX = Constants.getPackagePrefix()
            + ".javascript";

    private static final String MAIN_REPOSITORY_DIRECTORY_LOCATION_PARAMETER = PARAMETER_PREFIX
            + ".MAIN_REPOSITORY_DIRECTORY";

    private static final String REPOSITORY_DEV_MODE_PARAMETER = PARAMETER_PREFIX
            + ".REPOSITORY_DEV_MODE";

    private static final String REPOSITORIES_PARAMETER = PARAMETER_PREFIX
            + ".REPOSITORIES";

    private static final String NO_CACHE_PARAMETER = Constants
            .getPackagePrefix() + ".NO_CACHE";

    private static final String COMPILED_JS_SUFFIX_PARAMETER = Constants
            .getPackagePrefix() + ".COMPILED_JS_SUFFIX";

    private static final String CONFIGURATION_VERSION_PARAMETER = Constants
            .getPackagePrefix() + ".CONFIGURATION_VERSION";

    private static final String SYMBOLS_FILENAME = "/symbols";

    private static final String REPOSITORY_PROPERTY = "org.rcfaces.renderkit.html.javascript.DependenciesRepository";

    private static final String CONTEXT_REPOSITORY_PROPERTY = "org.rcfaces.renderkit.html.javascript.ContextRepository";

    private static final String JAVASCRIPT_SYMBOLS_PARAMETER = Constants
            .getPackagePrefix() + ".javascript.SYMBOLS";

    private static final DateFormat HEADER_DATE_FORMAT;
    static {
        HEADER_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.MEDIUM);
    }

    private static final Locale SYMBOL_LOCALE = new Locale("SYMBOLS");

    private static final ICriteria SYMBOL_CRITERIA = LocaleCriteria
            .get(SYMBOL_LOCALE);

    private static final String JAVASCRIPT_VERSION_PROPERTY = "javascript.version";

    private static final Set<String> SYMBOLS_FILENAMES = new HashSet<String>(2);

    private static final String JAVASCRIPT_REPOSITORY_TYPE = "javascript";

    static {
        SYMBOLS_FILENAMES.add(SYMBOLS_FILENAME);
    }

    private String mainRepositoryURI;

    private String htmlRCFacesBuildId;
    
    private List<IFile> symbolsFiles = null;

    private boolean enableSymbols = false;

    private long symbolsLastModified = 0;

    private String repositoryVersion;

    private ExpirationDate versionedExpirationDate;

    // Ne sert que dans la version de developpement
    private String compiledJsSuffix;

    private boolean profilerMode;

    private String mainRepositoryDirectoryLocation;

    public JavaScriptRepositoryServlet() {
    }

    public JavaScriptRepositoryServlet(String mainRepositoryURI) {
        this.mainRepositoryURI = mainRepositoryURI;
    }

    public void init(ServletConfig config) throws ServletException {

        mainRepositoryDirectoryLocation = config
                .getInitParameter(MAIN_REPOSITORY_DIRECTORY_LOCATION_PARAMETER);
        if (mainRepositoryDirectoryLocation == null) {
            mainRepositoryDirectoryLocation = MAIN_REPOSITORY_DIRECTORY_LOCATION;
        }

        htmlRCFacesBuildId = Constants.getBuildId();

        if (mainRepositoryURI == null) {
            mainRepositoryURI = ServletTools.computeResourceURI(
                    config.getServletContext(), null, getClass());
        }

        if (mainRepositoryURI == null) {
            LOG.error("Servlet '" + config.getServletName()
                    + "' is disabled because its URL can not be determined !");
            return;
        }

        super.init(config);

        compiledJsSuffix = getParameter(COMPILED_JS_SUFFIX_PARAMETER);
        if (compiledJsSuffix != null) {
            LOG.info("Compiled JS Suffix=" + compiledJsSuffix);
        }

        profilerMode = "true"
                .equalsIgnoreCase(getParameter(IHtmlProcessContext.PROFILER_MODE_APPLICATION_PARAMETER));
        LOG.info("Profiler mode=" + profilerMode);
    }

    protected boolean getVersionSupport() {
        return Constants.VERSIONED_FRAMEWORK_URL_SUPPORT;
    }

    protected String getParameterPrefix() {
        return PARAMETER_PREFIX;
    }

    protected String getNoCacheParameterName() {
        return NO_CACHE_PARAMETER;
    }

    protected String getRepositoryDevModeParameterName() {
        return REPOSITORY_DEV_MODE_PARAMETER;
    }

    protected String getDefaultCharset() {
        return Constants.JAVASCRIPT_DEFAULT_CHARSET;
    }

    protected String getContentType(Record record) {
        String contentType = IHtmlRenderContext.JAVASCRIPT_TYPE;

        String charset = record.getCharset();
        if (charset == null) {
            return contentType;
        }

        return contentType + "; charset=" + charset;
    }

    protected IRepository initializeRepository(ServletConfig config)
            throws IOException {
        ServletContext servletContext = config.getServletContext();

        if (Constants.VERSIONED_FRAMEWORK_URL_SUPPORT) {
            if (htmlRCFacesBuildId == null) {
                throw new FacesException(
                        "Can not enable \"Repository version\", rcfaces buildId is not detected !");
            }

            String buildId = htmlRCFacesBuildId;

            String compiledVersion = getCompiledJavascriptVersion();
            if (compiledVersion != null) {
                buildId += compiledVersion;
            }

            String configurationVersion = getParameter(CONFIGURATION_VERSION_PARAMETER);
            if (configurationVersion != null
                    && configurationVersion.length() > 0) {
                buildId += "." + configurationVersion;
            }

            this.repositoryVersion = buildId;

            LOG.info("Repository version buildId='" + htmlRCFacesBuildId
                    + "' compiledVersion=" + compiledVersion
                    + " setted for servlet '" + getServletName() + "'.");
        }

        Map<String, Object> applicationParamaters = new ApplicationParametersMap(
                servletContext);

        JavaScriptRepository repository = new JavaScriptRepository(
                mainRepositoryURI, repositoryVersion, applicationParamaters);
        servletContext.setAttribute(REPOSITORY_PROPERTY, repository);

        List<String> repositoriesLocation = new ArrayList<String>(32);
        // String location = getMainRepositoryDirectoryLocation()+
        // MAIN_REPOSITORY_LOCATION;
        // repositoriesLocation.add(location);
        // LOG.debug("Add repository location '" + location + "'. [main]");

        RcfacesContext rcfacesContext = RcfacesContext.getInstance(
                servletContext, null, null);
        String managerRepositories[] = rcfacesContext.getRepositoryManager()
                .listRepositoryLocations(JAVASCRIPT_REPOSITORY_TYPE);
        if (managerRepositories != null && managerRepositories.length > 0) {
            for (int i = 0; i < managerRepositories.length; i++) {
                String repositoryLocation = managerRepositories[i];

                repositoriesLocation.add(repositoryLocation);

                LOG.debug("Add repository location '" + repositoryLocation
                        + "'. [repository]");
            }
        }

        String repositoryParameter = config
                .getInitParameter(REPOSITORIES_PARAMETER);
        if (repositoryParameter != null) {
            for (StringTokenizer st = new StringTokenizer(repositoryParameter,
                    ","); st.hasMoreTokens();) {
                String repositoryLocation = st.nextToken().trim();

                if (repositoriesLocation.contains(repositoryLocation)) {
                    continue;
                }

                repositoriesLocation.add(repositoryLocation);

                LOG.debug("Add repository location '" + repositoryLocation
                        + "'. [config]");
            }
        }

        Object container = null;

        for (Iterator it = repositoriesLocation.iterator(); it.hasNext();) {
            String repositoryLocation = (String) it.next();

            LOG.debug("Load repository location '" + repositoryLocation
                    + "' ...");

            Object repositoryContainer = servletContext;
            InputStream in = servletContext
                    .getResourceAsStream(repositoryLocation);
            if (in == null) {
                ClassLoader cl = getClass().getClassLoader();

                Enumeration en = cl.getResources(repositoryLocation);
                if (en.hasMoreElements()) {
                    URL url = (URL) en.nextElement();

                    in = url.openStream();

                    if (in != null) {
                        repositoryContainer = getClass().getClassLoader();

                        if (en.hasMoreElements()) {
                            LOG.error("CAUTION: Same resource into differents locations ... ("
                                    + repositoryLocation + ")");
                        }
                    } else {
                        LOG.error("Can not load url '" + url + "'.");
                    }
                }
            }

            if (in == null) {
                LOG.info("Can not find repository '" + repositoryLocation
                        + "'.");
                continue;
            }

            if (container == null) {
                container = repositoryContainer;
            }

            try {
                repository.loadRepository(in, repositoryContainer);

            } finally {
                try {
                    in.close();

                } catch (IOException ex) {
                    LOG.error("Can not close '" + repositoryLocation + "'.", ex);
                }
            }

            LOG.debug("Repository location '" + repositoryLocation + "' loaded");
        }

        // Resolve
        repository.resolveDependencies();

        List<IFile> symbolsFiles = new ArrayList<IFile>();
        for (Iterator<ISymbolFile> it = repository.listSymbolsFiles()
                .iterator(); it.hasNext();) {
            ISymbolFile symbolFile = it.next();

            String baseDirectory = symbolFile.getBaseDirectory();
            if (baseDirectory.endsWith("/")) {
                baseDirectory = baseDirectory.substring(0,
                        baseDirectory.length() - 1);
            }

            IFile file = null;
            try {
                file = repository.declareFile(symbolFile.getSymbolsPath(),
                        baseDirectory, null, null, container, null);

                if (file != null) {
                    LOG.info("Javascript symbols detected.");

                    symbolsFiles.add(file);
                }

            } catch (IllegalArgumentException ex) {
                LOG.trace("Can not load symbols file", ex);
            }
        }

        if (symbolsFiles.isEmpty()) {
            this.symbolsFiles = null;
            return repository;
        }

        this.symbolsFiles = symbolsFiles;
        reloadSymbols(repository);

        return repository;
    }

    protected String getCompiledJavascriptVersion() {
        ServletContext servletContext = getServletContext();

        String symbolURL = getMainRepositoryDirectoryLocation()
                + SYMBOLS_FILENAME;

        URL url = ClassLocator.getResource(symbolURL, this, servletContext);
        if (url == null) {
            return null;
        }

        try {
            InputStream ins = url.openStream();
            try {
                Properties p = new Properties();

                p.load(ins);

                String version = p.getProperty(JAVASCRIPT_VERSION_PROPERTY);
                if (version != null) {
                    return version;
                }

            } finally {
                try {
                    ins.close();
                } catch (IOException ex2) {
                    LOG.debug(ex2);
                }
            }

        } catch (IOException ex) {
            LOG.error(ex);
        }

        return "c";
    }

    protected String getMainRepositoryDirectoryLocation() {
        return mainRepositoryDirectoryLocation;
    }

    protected String getSetURI(String setName) {
        return "vfs-" + setName + ".js";
    }

    protected Record newRecord(IFile file, ICriteria criteria) {
        return new JavaScriptRecord(file, criteria);
    }

    protected String getInputCharset() {
        return "UTF-8";
    }

    private String getOuputCharset() {
        return "UTF-8";
    }

    protected ISet initializeDefaultSet() {

        List<Object> mds = new ArrayList<Object>(8);

        IModule modules[] = getHierarchicalRepository().listModules();

        for (int i = 0; i < modules.length; i++) {
            IModule module = modules[i];

            if (module.isDefaultCoreModule() == false) {
                continue;
            }

            module.setGroupAllFiles(true);
            mds.add(module);
        }

        IContext context = getHierarchicalRepository().createContext(null);

        IHierarchicalFile ret[] = getHierarchicalRepository().computeFiles(mds,
                IHierarchicalRepository.FILE_COLLECTION_TYPE, context);

        String name = getBootSetDefaultValue();

        String uri = getSetURI(name);

        mds = Arrays.<Object> asList((Object[]) ret);

        ISet set = getHierarchicalRepository().declareSet(name, uri,
                mds.toArray(new IModule[mds.size()]));

        return set;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
     */
    private class JavaScriptRecord extends HierarchicalRecord {

        protected byte prolog[];

        protected byte epilog[];

        public JavaScriptRecord(IFile file, ICriteria criteria) {
            super(file, criteria);
        }

        public ExpirationDate getExpirationDate() {
            if (repositoryVersion != null && versionedExpirationDate != null) {
                return versionedExpirationDate;
            }

            return super.getExpirationDate();
        }

        protected IContentRef[] getFileContentReferences(IFile file) {
            // On peut tenter ici de rechercher la version compil�e !

            IContentRef urls[] = file.getContentReferences(criteria);

            if (symbolsFiles == null) {
                return urls;
            }

            URLContentRef ucr0 = (URLContentRef) urls[0];
            String surl0 = ucr0.getURL().toString();
            if (surl0.endsWith(".js") == false) {
                return urls;
            }

            IContentRef urlsc[] = new IContentRef[urls.length];

            for (int i = 0; i < urls.length; i++) {
                URLContentRef ucr = (URLContentRef) urls[i];

                String surl = ucr.getURL().toString(); // + "c";
                if (compiledJsSuffix != null) {
                    surl += compiledJsSuffix;
                }

                URL url;
                try {
                    url = new URL(surl);

                } catch (MalformedURLException e) {
                    LOG.error("Can not get URL of '" + surl + "'.", e);
                    continue;
                }

                try {
                    IContent content = file.getContentProvider()
                            .getContent(ucr);
                    try {
                        InputStream ins = content.getInputStream();
                        if (ins != null) {
                            try {
                                ins.close();
                            } catch (IOException ex2) {
                            }

                            urlsc[i] = new URLContentRef(ucr.getCriteria(), url);

                            LOG.debug("Use compiled record of '"
                                    + file.getFilename() + "' (" + surl + ")");
                        }
                    } finally {
                        content.release();
                    }

                } catch (IOException ex) {
                    LOG.error(
                            "Can not get connection of '" + file.getFilename()
                                    + "'. (" + surl + ")", ex);
                }
            }

            return urlsc;
        }

        protected byte[] updateBuffer(byte[] buffer) throws IOException {

            String fileName = getFile().getFilename();

            prolog = null;
            if (symbolsFiles != null && symbolsFiles.contains(fileName)) {
                JavaScriptRepositoryServlet.this.reloadSymbols(getRepository());
            }

            String content = new String(buffer, getInputCharset());

            if (symbolsFiles != null) {
                content = ((IClassFile) file).convertSymbols(getSymbols(),
                        content);
            }

            return content.getBytes(getOuputCharset());
        }

        public final byte[] getProlog() throws IOException {
            if (prolog != null) {
                return prolog;
            }

            StringAppender sb = new StringAppender(128);
            fillProlog(sb);

            prolog = sb.toString().getBytes(getOuputCharset());

            return prolog;
        }

        public final byte[] getEpilog() throws IOException {
            if (epilog != null) {
                return epilog;
            }

            StringAppender sb = new StringAppender(128);
            fillEpilog(sb);

            epilog = sb.toString().getBytes(getOuputCharset());

            return epilog;
        }

        private void fillEpilog(StringAppender sb) {

            // if (isMultiWindowScript(getServletContext())) {
            writeBundles(sb, (IHierarchicalFile) getFile(), criteria);
            // }

            Map map = getSymbols();

            int cnt = 0;
            for (int i = 0; i < CLEAR_VARIABLES.length; i++) {
                String var = CLEAR_VARIABLES[i];

                if (map != null) {
                    String cvar = (String) map.get(var);
                    if (cvar != null) {
                        var = cvar;
                    }
                }

                if (cnt > 0) {
                    sb.append('=');
                }
                sb.append(var);
                cnt++;
            }

            if (cnt > 0) {
                sb.append("=undefined;\n");
            }
        }

        protected void fillProlog(StringAppender sb) throws IOException {
            if (Constants.JAVASCRIPT_APPEND_RCFACES_HEADER) {
                if (htmlRCFacesBuildId != null) {
                    sb.append("var rcfacesBuildId=\"");
                    sb.append(htmlRCFacesBuildId);
                    sb.append("\";");
                }

                sb.append("// RCFaces Components:");

                long date = getLastModificationDate();
                if (date > 0) {
                    sb.append(" lastModification=");
                    sb.append(HEADER_DATE_FORMAT.format(new Date(date)));
                }

                sb.append('\n');
            }

            if (Constants.JAVASCRIPT_APPEND_PROFILER_DATE) {
                if (profilerMode) {
                    sb.append("window._rcfacesBundleTime=new Date();\n");
                }
            }
            
            //JSF version
            
            sb.append("var _rcfaces_jsfVersion=\"");
            if (RcfacesContext.isJSF2_0()) {
            	sb.append("2.0\"");
            } else if (RcfacesContext.isJSF1_2()) {
            	sb.append("1.2\"");
            }
            sb.append('\n');
        }

        public String getCharset() {
            return getOuputCharset();
        }
    }

    /*
     * public static final IJavaScriptRepository getRepository( ServletContext
     * servletContext) { IJavaScriptRepository javaScriptRepository =
     * (IJavaScriptRepository) servletContext
     * .getAttribute(REPOSITORY_PROPERTY);
     * 
     * if (javaScriptRepository == null) { throw new FacesException( "Javascript
     * repository is not initialized !"); }
     * 
     * return javaScriptRepository; }
     */

    public static final IJavaScriptRepository getRepository(
            FacesContext facesContext) {
        Map map = facesContext.getExternalContext().getApplicationMap();

        IJavaScriptRepository javaScriptRepository = (IJavaScriptRepository) map
                .get(REPOSITORY_PROPERTY);

        if (javaScriptRepository == null) {
            throw new FacesException(
                    "Javascript repository is not initialized !");
        }

        return javaScriptRepository;
    }

    public void writeBundles(StringAppender sb, IHierarchicalFile file,
            ICriteria criteria) {
        IHierarchicalFile dependencies[];
        if (file instanceof IHierarchicalRepository.ISet) {
            dependencies = ((IHierarchicalRepository.ISet) file)
                    .listDependencies();

        } else if (file instanceof IHierarchicalRepository.IModule) {
            dependencies = ((IHierarchicalRepository.IModule) file)
                    .listDependencies();

        } else {
            dependencies = new IHierarchicalFile[] { file };
        }

        List<IClass> classes = new ArrayList<IClass>(dependencies.length);
        for (int i = 0; i < dependencies.length; i++) {
            IFile dependency = dependencies[i];
            if ((dependency instanceof IJavaScriptRepository.IClassFile) == false) {
                continue;
            }
            IJavaScriptRepository.IClassFile cf = (IJavaScriptRepository.IClassFile) dependency;

            classes.addAll(Arrays.asList(cf.listClasses()));
        }

        if (classes.isEmpty()) {
            return;
        }

        sb.append("new f_bundle(window, \"");

        String furi = file.getURI(criteria);
        if (furi == null) {
            throw new NullPointerException("Can not get URI of file '" + file
                    + "' criteria='" + criteria + "'");
        }
        sb.append(furi);
        sb.append('\"');

        Map<String, String> symbols = null;
        if (symbolsFiles != null) {
            symbols = getSymbols();
        }

        sb.append(",[");

        boolean first = true;
        for (Iterator it = classes.iterator(); it.hasNext();) {
            IClass clazz = (IClass) it.next();

            if (first) {
                first = false;
            } else {
                sb.append(',');
            }

            String className = clazz.getName();
            if (symbols != null) {
            	 String cn = symbols.get(className);

                if (cn != null) {
                    className = cn;
                }
            }
            sb.append(className);
        }

        sb.append("]);\n");
    }

    public static IRepository.IContext getContextRepository(
            FacesContext facesContext) {
        Map<String, Object> map = facesContext.getExternalContext()
                .getRequestMap();

        IRepository.IContext context = (IRepository.IContext) map
                .get(CONTEXT_REPOSITORY_PROPERTY);
        if (context != null) {
            return context;
        }

        ICriteria criteria = null;

        Locale locale = ContextTools.getUserLocale(facesContext);
        if (locale != null) {
            criteria = LocaleCriteria.get(criteria, locale);
        }

        IUserAgent userAgent = ClientBrowserFactory.Get().get(facesContext);
        if (userAgent != null) {
            criteria = UserAgentCriteria.get(criteria, userAgent);
        }

        context = getRepository(facesContext).createContext(criteria);
        map.put(CONTEXT_REPOSITORY_PROPERTY, context);

        return context;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getSymbols(FacesContext facesContext) {
        Map applicationMap = facesContext.getExternalContext()
                .getApplicationMap();

        return (Map<String, String>) applicationMap
                .get(JAVASCRIPT_SYMBOLS_PARAMETER);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getSymbols(ServletContext servletContext) {
        return (Map<String, String>) servletContext
                .getAttribute(JAVASCRIPT_SYMBOLS_PARAMETER);
    }

    protected final Map<String, String> getSymbols() {
        return getSymbols(getServletContext());
    }

    @SuppressWarnings("null")
    private void reloadSymbols(IRepository repository) throws IOException {

        Map<String, String> symbols = new HashMap<String, String>(4096);

        long symbolsLastModified = 0;

        for (Iterator<IFile> it = this.symbolsFiles.iterator(); it.hasNext();) {
            IFile file = it.next();

            byte buffer[] = null;

            Record record = getFileRecord(file, SYMBOL_CRITERIA);
            if (record != null) {
                buffer = record.getBuffer();
            }

            if (buffer == null || buffer.length == 0) {
                continue;
            }

            loadSymbols(symbols, buffer);
            if (record.getLastModificationDate() > this.symbolsLastModified) {
                symbolsLastModified = record.getLastModificationDate();
            }
        }

        if (symbolsLastModified == 0) {
            // No modifications !
            return;
        }
        this.symbolsLastModified = symbolsLastModified;

        getServletContext().setAttribute(JAVASCRIPT_SYMBOLS_PARAMETER, symbols);
    }

    @SuppressWarnings("unchecked")
    private void loadSymbols(Map<String, String> symbols, byte[] buffer)
            throws IOException {

        InputStream bin = new ByteBufferInputStream(buffer);

        Properties properties = new Properties();
        properties.load(bin);

        bin.close();

        // Merci les génériques à la sauce JAVA
        Map propertiesMap = properties;
        symbols.putAll(propertiesMap);

    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        if (mainRepositoryURI == null) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        super.service(request, response);
    }

    protected String getBootSetDefaultValue() {
        return Constants.JAVASCRIPT_BOOT_SET_DEFAULT_VALUE;
    }

    protected String getGroupAllDefaultValue() {
        return Constants.JAVASCRIPT_GROUP_ALL_DEFAULT_VALUE;
    }

}
