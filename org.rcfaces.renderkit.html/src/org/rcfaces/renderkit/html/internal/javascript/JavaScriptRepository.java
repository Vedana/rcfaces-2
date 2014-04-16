/*
 * $Id: JavaScriptRepository.java,v 1.4 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.javascript;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.Services;
import org.rcfaces.core.internal.contentProxy.IResourceProxyHandler;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.repository.BasicHierarchicalRepository;
import org.rcfaces.core.internal.repository.IContentRef;
import org.rcfaces.core.internal.repository.LocaleCriteria;
import org.rcfaces.core.internal.repository.URLContentRef;
import org.rcfaces.core.internal.util.FilteredContentProvider;
import org.rcfaces.core.internal.util.LocalizedURLContentProvider;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
 */
public class JavaScriptRepository extends BasicHierarchicalRepository implements
        IJavaScriptRepository {

    private static final long serialVersionUID = 7359720004642078904L;

    private static final Log LOG = LogFactory
            .getLog(JavaScriptRepository.class);

    private static final IClass[] CLASS_EMPTY_ARRAY = new IClass[0];

    private static final String JAVASCRIPT_BASE_URI_PROPERTY = "org.rfaces.core.repository.JAVASCRIPT_BASE_URI";

    // private static final boolean KEEP_ONLY_LANGUAGE_LOCALE = false;

    private static final ICriteria DEFAULT_CRITERIA = LocaleCriteria
            .get(Constants.REPOSITORY_DEFAULT_LOCALE);

    private final Map<String, IClass> classByName = new HashMap<String, IClass>();

    private final Map dependenciesById = new HashMap();

    private boolean convertSymbols = false;

    private String parsingBundleBaseName = null;

    private final Map<String, Object> applicationParameters;

    private boolean hasResourceBundleName;
    
    private List<ISymbolFile> symbolsFiles = new ArrayList<ISymbolFile>();

    public JavaScriptRepository(String servletURI, String repositoryVersion,
            Map<String, Object> applicationParameters) {
        super(servletURI, repositoryVersion);

        this.applicationParameters = applicationParameters;
    }
    
    public List<ISymbolFile> listSymbolsFiles() {
        return symbolsFiles;
    }

    @Override
    protected IContentProvider getDefaultContentProvider() {
        return LocalizedURLContentProvider.SINGLETON;
    }

    @Override
    protected FileByCriteria createFileByCriteria(IFile file,
            IContentProvider contentProvider, ICriteria criteria, String uri,
            IContentRef noCriteriaContentLocation) {

        if (hasResourceBundleName) {
            ICriteria localeCriteria = LocaleCriteria.keepLocale(criteria);

            if (localeCriteria != null) {
                return new JavaScriptCriteriaFile(file, contentProvider,
                        localeCriteria, uri, noCriteriaContentLocation);
            }
        }

        return super.createFileByCriteria(file, contentProvider, criteria, uri,
                noCriteriaContentLocation);
    }

    public void loadRepository(InputStream input, Object container) {
        super.loadRepository(input, container);

        String moduleName = AbstractFacesImplementation.get()
                .getJavaScriptModuleName();

        if (moduleName != null) {
            Module coreModule = (Module) getModuleByName("core");
            if (coreModule != null) {
                IModule module = getModuleByName(moduleName);
                if (module != null) {
                    IHierarchicalFile deps[] = module.listDependencies();
                    for (int i = 0; i < deps.length; i++) {
                        coreModule.addFile(deps[i]);
                    }
                }
            }
        }
    }

    @Override
    protected ICriteria getDefaultRepositoryCriteria() {
        return DEFAULT_CRITERIA;
    }

    protected HierarchicalFile createFile(IModule module, String name,
            String filename, String noCriteriaURI,
            IContentRef noCriteriaContentLocation,
            IHierarchicalFile dependencies[], IContentProvider contentProvider) {

        return new JavaScriptFile(module, name, filename, noCriteriaURI,
                noCriteriaContentLocation, dependencies, contentProvider,
                convertSymbols, parsingBundleBaseName);
    }

    public IClass getClassByName(String className) {
        return classByName.get(className);
    }

    protected IHierarchicalFile convertType(Object next, int typeOfCollection) {
        if (typeOfCollection == CLASS_NAME_COLLECTION_TYPE) {
            String className = (String) next;

            IClass clazz = getClassByName(className);
            if (clazz == null) {
                throw new NullPointerException("Unknown class '" + className
                        + "'.");
            }
            return clazz.getFile();
        }

        return super.convertType(next, typeOfCollection);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
     */
    protected class JavaScriptFile extends HierarchicalFile implements
            IClassFile {

        private static final long serialVersionUID = 4826077949811747989L;

        private List<IClass> classes;

        private final boolean remapSymbols;

        private final String resourceBundleBaseName;

        private final LocalizedContentProvider specifiedContentProvider;

        public JavaScriptFile(IModule module, String name, String filename,
                String noCriteriaURI, IContentRef noCriteriaContentLocation,
                IHierarchicalFile[] dependencies,
                IContentProvider contentProvider, boolean remapSymbols,
                String resourceBundleBaseName) {
            super(module, name, filename, noCriteriaURI,
                    noCriteriaContentLocation, dependencies, contentProvider);

            this.remapSymbols = remapSymbols;
            this.resourceBundleBaseName = resourceBundleBaseName;

            if (contentProvider == null && resourceBundleBaseName != null) {
                this.specifiedContentProvider = new LocalizedContentProvider(
                        this);

            } else {
                this.specifiedContentProvider = null;
            }
        }

        @Override
        public ICriteria getSupportedCriteria(ICriteria criteria) {
            // TODO Auto-generated method stub
            return super.getSupportedCriteria(criteria);
        }

        public String getResourceBundleBaseName() {
            return resourceBundleBaseName;
        }

        @Override
        public String getURI(ICriteria criteria) {
            String uri = super.getURI(criteria);
            if (resourceBundleBaseName == null
                    && specifiedContentProvider == null) {
                return uri;
            }

            ICriteria selectedCriteria = getSupportedCriteria(criteria);
            if (selectedCriteria != null) {
                return super.getURI(selectedCriteria);
            }

            return uri;
        }

        public IClass[] listClasses() {
            if (classes == null || classes.isEmpty()) {
                return CLASS_EMPTY_ARRAY;
            }

            return classes.toArray(new IClass[classes.size()]);
        }

        public void addClass(IClass name) {
            if (classes == null) {
                classes = new ArrayList<IClass>(4);
            }

            classes.add(name);
        }

        public String convertSymbols(Map<String, String> symbols, String code) {
            if (remapSymbols == false || symbols == null) {
                return code;
            }

            IJavaScriptSymbolsConverter provider = (IJavaScriptSymbolsConverter) Services
                    .get().getService(IJavaScriptSymbolsConverter.SERVICE_ID);

            if (provider == null) {
                return code;
            }

            return provider.convertSymbols(getId(), code, symbols,
                    applicationParameters);
        }

        @Override
        public IContentProvider getContentProvider() {
            if (specifiedContentProvider != null) {
                return specifiedContentProvider;
            }

            return super.getContentProvider();
        }
    }

    /*
     * protected ICriteria adaptCriteria(ICriteria criteria, IFile file) { if
     * (KEEP_ONLY_LANGUAGE_LOCALE) { if (criteria != null) { Locale locale =
     * LocaleCriteria.getLocale(criteria); if (locale != null &&
     * (locale.getCountry().length() > 0 || locale .getVariant().length() > 0))
     * { criteria = LocaleCriteria.get(new Locale(locale .getLanguage())); } } }
     * 
     * return super.adaptCriteria(criteria, file); }
     */

    protected void addRules(Digester digester, Object container) {
        super.addRules(digester, container);

        digester.addRule("repository", new Rule() {

            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {
                super.begin(namespace, name, attributes);

                String convertSymbolsValue = attributes
                        .getValue("remapSymbols");

                convertSymbols = (convertSymbolsValue == null)
                        || ("true".equalsIgnoreCase(convertSymbolsValue));

                parsingBundleBaseName = attributes
                        .getValue("resourcesBundleBaseName");

                if (parsingBundleBaseName != null) {
                    hasResourceBundleName = true;
                }
                
                String baseDirectory = attributes.getValue("baseDirectory");

                String symbolsPath = attributes.getValue("symbolsPath");
                if (symbolsPath != null && baseDirectory != null) {
                    symbolsFiles
                            .add(new SymbolFile(symbolsPath, baseDirectory));
                }
            }

            public void end(String namespace, String name) throws Exception {

                convertSymbols = false;
                parsingBundleBaseName = null;

                super.end(namespace, name);
            }

        });

        digester.addRule("repository/module/file/class", new Rule() {

            public void begin(String namespace, String xname,
                    Attributes attributes) throws Exception {

                String name = attributes.getValue("name");
                if (name == null) {
                    throw new IllegalArgumentException(
                            "No 'name' attribute for <class> element !");
                }

                JavaScriptFile javaScriptFile = (JavaScriptFile) this.digester
                        .peek();

                IClass clazz = new ClassImpl(name, javaScriptFile);

                javaScriptFile.addClass(clazz);

                if (classByName.put(name, clazz) != null) {
                    LOG.error("Class '" + name + "' is already known !");
                }

                this.digester.push(clazz);
            }

            public void end(String namespace, String name) throws Exception {

                // ClassImpl clazz = (ClassImpl) this.digester.peek();

                /*
                 * List l = null;
                 * 
                 * IClass cls[] = clazz.listRequiredClasses(null); for (int i =
                 * 0; i < cls.length; i++) { IHierarchicalFile file =
                 * cls[i].getFile();
                 * 
                 * if (l == null) { l = new ArrayList(); } l.add(file); }
                 * 
                 * IHierarchicalFile resources[] = clazz
                 * .listRequiredResources(null); if (resources.length > 0) { if
                 * (l == null) { l = new ArrayList(); }
                 * l.addAll(Arrays.asList(resources)); }
                 * 
                 * if (l != null) { ((HierarchicalFile) clazz.getFile())
                 * .addDependencies((IHierarchicalFile[]) l .toArray(new
                 * IHierarchicalFile[l.size()])); }
                 */

                this.digester.pop();
            }
        });

        digester.addRule("repository/module/file/class/required-class",
                new Rule() {

                    public void begin(String namespace, String xname,
                            Attributes attributes) throws Exception {

                        String name = attributes.getValue("name");
                        if (name == null) {
                            throw new IllegalArgumentException(
                                    "No 'name' attribute for <class> element !");
                        }

                        String id = attributes.getValue("id");

                        ClassImpl clazz = (ClassImpl) this.digester.peek();

                        clazz.addRequiredClass(id, name);
                    }
                });
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
     */
    protected static class ClassImpl implements IClass {

        private final String name;

        private final IHierarchicalFile file;

        private List<IClass> requiredClass;

        private List<String> requiredClassByName;

        private IClass requiredClassArray[];

        private Map<String, Object> requiredClassById = new HashMap<String, Object>();

        public ClassImpl(String name, IHierarchicalFile file) {
            this.file = file;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addRequiredClass(String requiredId, String clazz) {
            if (requiredClassByName == null) {
                requiredClassByName = new ArrayList<String>();
            }

            requiredClassByName.add(requiredId);
            requiredClassByName.add(clazz);
        }

        void resolve(Map classesByName) {
            if (requiredClassByName == null) {
                return;
            }

            List l2 = requiredClassByName;
            requiredClassByName = null;

            for (Iterator it = l2.iterator(); it.hasNext();) {
                String requiredId = (String) it.next();
                String className = (String) it.next();

                ClassImpl clazz = (ClassImpl) classesByName.get(className);
                if (clazz == null) {
                    throw new FacesException("Can not find class '" + className
                            + "' to link requirements.");
                }

                clazz.resolve(classesByName);

                addRequiredClass(requiredId, clazz);
            }

            List<IHierarchicalFile> l = null;

            IClass cls[] = listRequiredClasses(null);
            for (int i = 0; i < cls.length; i++) {
                IHierarchicalFile file = cls[i].getFile();

                if (l == null) {
                    l = new ArrayList<IHierarchicalFile>();
                }
                l.add(file);
            }

            IHierarchicalFile resources[] = listRequiredResources(null);
            if (resources.length > 0) {
                if (l == null) {
                    l = new ArrayList<IHierarchicalFile>();
                }
                l.addAll(Arrays.asList(resources));
            }

            if (l != null) {
                ((HierarchicalFile) getFile()).addDependencies(l
                        .toArray(new IHierarchicalFile[l.size()]));
            }
        }

        @SuppressWarnings("unchecked")
        private void addRequiredClass(String requiredId, IClass clazz) {
            if (requiredId == null) {
                if (requiredClass == null) {
                    requiredClass = new ArrayList<IClass>();
                }

                requiredClass.add(clazz);
                return;
            }

            List<IClass> requiredClass = (List<IClass>) requiredClassById
                    .get(requiredId);
            if (requiredClass == null) {
                requiredClass = new ArrayList<IClass>();
                requiredClassById.put(requiredId, requiredClass);
            }

            requiredClass.add(clazz);
        }

        @SuppressWarnings("unchecked")
        public IClass[] listRequiredClasses(String requiredId) {
            if (requiredClassByName != null) {
                throw new IllegalStateException("Class linkage not processed !");
            }

            if (requiredId == null) {
                if (requiredClassArray != null) {
                    return requiredClassArray;
                }
                if (requiredClass == null) {
                    requiredClassArray = CLASS_EMPTY_ARRAY;
                    return requiredClassArray;
                }

                listInheritRequiredClasses(requiredClass, null);

                requiredClassArray = requiredClass
                        .toArray(new IClass[requiredClass.size()]);
                requiredClass = null;
                return requiredClassArray;
            }

            Object obj = requiredClassById.get(requiredId);
            if (obj instanceof IClass[]) {
                return (IClass[]) obj;
            }

            List<IClass> l = (List<IClass>) obj;
            if (l == null) {
                l = new ArrayList<IClass>();
            }
            listInheritRequiredClasses(l, requiredId);

            IClass requiredClassArray[];

            if (l.isEmpty()) {
                requiredClassArray = CLASS_EMPTY_ARRAY;

            } else {
                requiredClassArray = l.toArray(new IClass[l.size()]);
            }

            requiredClassById.put(requiredId, requiredClassArray);

            return requiredClassArray;
        }

        public void listInheritRequiredClasses(List<IClass> l, String requiredId) {
            Set<IClass> l2 = new HashSet<IClass>(l);
            if (requiredId != null) {
                l2.addAll(Arrays.asList(listRequiredClasses(null)));
            }

            for (Iterator it = l2.iterator(); it.hasNext();) {
                IClass clz = (IClass) it.next();

                IClass rcs[] = clz.listRequiredClasses(requiredId);
                if (rcs.length < 1) {
                    continue;
                }

                l.addAll(Arrays.asList(rcs));
            }
        }

        public IHierarchicalFile[] listRequiredResources(String requiredId) {
            return HIERARCHICAL_FILE_EMPTY_ARRAY;
        }

        public IHierarchicalFile getFile() {
            return file;
        }

        public String getResourceBundleName() {
            return ((JavaScriptFile) getFile()).resourceBundleBaseName;
        }

    }

    public String getBaseURI(IProcessContext processContext) {
        FacesContext facesContext = processContext.getFacesContext();

        ExternalContext ext = facesContext.getExternalContext();

        Map<String, Object> request = ext.getRequestMap();
        String uri = (String) request.get(JAVASCRIPT_BASE_URI_PROPERTY);
        if (uri != null) {
            return uri;
        }

        IResourceProxyHandler resourceProxyHandler = processContext
                .getRcfacesContext().getResourceProxyHandler();
        if (resourceProxyHandler != null && resourceProxyHandler.isEnabled()
                && resourceProxyHandler.isFrameworkResourcesEnabled()) {

            StringAppender sa = new StringAppender(256);

            // Il nous faut une URL en context path type
            sa.append(servletURI);

            if (repositoryVersion != null && repositoryVersion.length() > 0) {
                sa.append('/');
                sa.append(repositoryVersion);
            }

            uri = resourceProxyHandler.computeProxyedURL(facesContext, null,
                    null, sa.toString());
        }

        if (uri == null) {
            StringAppender sa = new StringAppender(256);

            sa.append(ext.getRequestContextPath());
            sa.append(servletURI);

            if (repositoryVersion != null && repositoryVersion.length() > 0) {
                sa.append('/');
                sa.append(repositoryVersion);
            }

            uri = sa.toString();
        }

        request.put(JAVASCRIPT_BASE_URI_PROPERTY, uri);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set Javascript repository URL to '" + uri + "'.");
        }
        return uri;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:32 $
     */
    public class LocalizedContentProvider extends FilteredContentProvider {

        private final JavaScriptFile javaScriptFile;

        public LocalizedContentProvider(JavaScriptFile javaScriptFile) {
            this.javaScriptFile = javaScriptFile;
        }

        @Override
        public IContentRef[] searchCriteriaContentReference(
                IContentRef contentReference, ICriteria criteria) {

            URLContentRef uric = (URLContentRef) contentReference;

            if (javaScriptFile.resourceBundleBaseName == null) {
                return null;
            }

            IClass cls[] = javaScriptFile.listClasses();

            if (cls.length == 0) {
                return null;
            }
            Locale locale = LocaleCriteria.getLocale(criteria);
            if (locale == null) {
                return null;
            }

            ResourceBundle resourceBundle = ResourceBundle.getBundle(
                    javaScriptFile.resourceBundleBaseName, locale);

            boolean found = false;

            classes: for (int i = 0; i < cls.length; i++) {
                String className = cls[i].getName();

                String key = className + ".";

                Enumeration keys = resourceBundle.getKeys();
                for (; keys.hasMoreElements();) {
                    String resourceKey = (String) keys.nextElement();
                    if (resourceKey.startsWith(key) == false) {
                        continue;
                    }

                    found = true;
                    break classes;
                }
            }

            if (found == false) {
                return null;
            }

            // Cela permet de retirer les autres criteres attachés !
            ICriteria selectedCriteria = LocaleCriteria.keepLocale(criteria);

            return new IContentRef[] { new URLContentRef(selectedCriteria,
                    uric.getURL()) };
        }

        protected String updateBuffer(String buffer, URL url, ICriteria criteria) {
            if (criteria == null) {
                return super.updateBuffer(buffer, url, criteria);
            }

            IClass cls[] = javaScriptFile.listClasses();
            if (cls.length < 1) {
                return super.updateBuffer(buffer, url, criteria);
            }

            Locale locale = LocaleCriteria.getLocale(criteria);
            if (locale != null) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(
                        javaScriptFile.resourceBundleBaseName, locale);

                StringAppender sa = new StringAppender(buffer, 8000);

                for (int i = 0; i < cls.length; i++) {
                    String className = cls[i].getName();

                    String key = className + ".";

                    boolean first = true;

                    Enumeration keys = resourceBundle.getKeys();
                    for (; keys.hasMoreElements();) {
                        String resourceKey = (String) keys.nextElement();
                        if (resourceKey.startsWith(key) == false) {
                            continue;
                        }

                        String value = resourceBundle.getString(resourceKey);

                        if (first) {
                            first = false;

                            sa.append("f_resourceBundle.Define2(\"")
                                    .append(className).append("\",{\n");
                        } else {
                            sa.append(",\n");
                        }

                        sa.append(resourceKey.substring(key.length())).append(
                                ':');

                        appendString(sa, value);
                    }

                    if (first == false) {
                        sa.append("\n});\n");
                    }
                }

                buffer = sa.toString();
            }

            return super.updateBuffer(buffer, url, criteria);
        }
    }

    private static StringAppender appendString(StringAppender sa, String str) {
        char escape = '\"';
        if (str.indexOf('\"') >= 0 && str.indexOf('\'') < 0) {
            escape = '\'';
        }

        sa.append(escape);

        char chs[] = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c == '\n') {
                sa.append("\\n");
                continue;
            }
            if (c == '\r') {
                sa.append("\\r");
                continue;
            }
            if (c == escape) {
                sa.append('\\').append(escape);
                continue;
            }
            if (c == '\t') {
                sa.append("\\t");
                continue;
            }
            if (c == '\\') {
                sa.append("\\\\");
                continue;
            }

            sa.append(c);
        }

        sa.append(escape);

        return sa;
    }

    public void resolveDependencies() {
        for (Iterator it = classByName.values().iterator(); it.hasNext();) {
            ClassImpl clazz = (ClassImpl) it.next();

            clazz.resolve(classByName);
        }
    }

    public class JavaScriptCriteriaFile extends FileByCriteria {

        public JavaScriptCriteriaFile(IFile file,
                IContentProvider contentProvider, ICriteria criteria,
                String uri, IContentRef noCriteriaContentLocation) {
            super(file, contentProvider, criteria, uri,
                    noCriteriaContentLocation);
        }
    }
    
    public class SymbolFile implements ISymbolFile {
        private final String path;

        private final String baseDirectory;

        public SymbolFile(String symbolsPath, String baseDirectory) {
            this.path = symbolsPath;
            this.baseDirectory = baseDirectory;
        }

        public String getSymbolsPath() {
            return path;
        }

        public String getBaseDirectory() {
            return baseDirectory;
        }
    }
}
