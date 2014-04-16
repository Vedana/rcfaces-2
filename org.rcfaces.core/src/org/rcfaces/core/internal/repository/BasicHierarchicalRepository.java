/*
 * $Id: BasicHierarchicalRepository.java,v 1.4 2013/11/13 12:53:24 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.core.internal.util.URLContentProvider;
import org.xml.sax.Attributes;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
 */
public class BasicHierarchicalRepository extends AbstractRepository implements
        IHierarchicalRepository {

    private static final long serialVersionUID = -6882051141540673466L;

    private static final Log LOG = LogFactory
            .getLog(BasicHierarchicalRepository.class);

    protected static final IHierarchicalFile[] HIERARCHICAL_FILE_EMPTY_ARRAY = new IHierarchicalFile[0];

    private static final IModule[] MODULE_EMPTY_ARRAY = new IModule[0];

    private static final Integer FILE_TYPE = new Integer(0);

    private static final Integer MODULE_TYPE = new Integer(1);

    private static final Integer SET_TYPE = new Integer(2);

    private final Map<String, IModule> modulesByName = new HashMap<String, IModule>();

    private final Map<String, ISet> setsByName = new HashMap<String, ISet>();

    private final Map<String, IFile> resourcesByName = new HashMap<String, IFile>();

    private ISet bootSet;

    public BasicHierarchicalRepository(String servletURI,
            String repositoryVersion) {
        super(servletURI, repositoryVersion);
    }

    public ISet getBootSet() {
        return bootSet;
    }

    public void setBootSet(ISet set) {
        this.bootSet = set;
    }

    @Override
    protected IContentProvider getDefaultContentProvider() {
        return URLContentProvider.SINGLETON;
    }

    public void loadRepository(InputStream input, Object container) {

        Digester digester = new Digester();

        addRules(digester, container);

        try {
            digester.parse(input);

        } catch (Exception ex) {
            LOG.error("Can not parse '" + container + "' ", ex);
        }
    }

    protected void addRules(Digester digester, final Object container) {

        final String baseDirectory[] = new String[1];

        digester.addRule("repository", new Rule() {

            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                String contentLocationDirectory = attributes
                        .getValue("baseDirectory");

                if (contentLocationDirectory != null) {
                    if (contentLocationDirectory.length() > 0
                            && contentLocationDirectory.endsWith("/") == false) {
                        contentLocationDirectory += "/";
                    }

                    baseDirectory[0] = contentLocationDirectory;
                }

            }
        });

        digester.addRule("repository/module", new Rule() {

            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {
                String id = attributes.getValue("id");
                if (id == null) {
                    throw new IllegalArgumentException(
                            "No 'id' attribute for <module> element !");
                }

                String filename = getModuleFilename(id);
                String uri = getURI(filename);
                boolean groupAllFiles = "true".equalsIgnoreCase(attributes
                        .getValue("groupAll"));

                String rid = "m:" + id;

                IModule m = new Module(rid, filename, uri, groupAllFiles);

                if ("true".equals(attributes.getValue("defaultCoreModule"))) {
                    m.setDefaultCoreModule();
                }

                filesByURI.put(uri, m);
                modulesByName.put(id, m);
                resourcesByName.put(rid, m);

                this.digester.push(m);
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                this.digester.pop();
            }

        });

        digester.addRule("repository/module/file", new Rule() {

            @Override
            public void begin(String namespace, String xname,
                    Attributes attributes) throws Exception {

                String name = attributes.getValue("name");
                if (name == null) {
                    throw new IllegalArgumentException(
                            "No 'name' attribute for <file> element !");
                }

                IHierarchicalFile ds[] = HIERARCHICAL_FILE_EMPTY_ARRAY;

                String depends = attributes.getValue("depends");
                if (depends != null) {
                    List<IHierarchicalFile> l = null;
                    for (StringTokenizer st = new StringTokenizer(depends, ", "); st
                            .hasMoreTokens();) {
                        String dname = st.nextToken();

                        IHierarchicalFile fd = (IHierarchicalFile) getFileByName(dname);
                        if (fd == null) {
                            throw new IllegalArgumentException("Can not find '"
                                    + dname + "' referenced by file '" + name
                                    + "' !");
                        }

                        if (l == null) {
                            l = new ArrayList<IHierarchicalFile>();
                        }

                        l.add(fd);
                    }

                    if (l != null) {
                        ds = l.toArray(new IHierarchicalFile[l.size()]);
                    }
                }

                IContentProvider contentProvider = null;
                String contentProviderClassName = attributes
                        .getValue("contentProvider");

                if (contentProviderClassName != null) {
                    try {
                        Class< ? extends IContentProvider> clazz = ClassLocator
                                .load(contentProviderClassName, null,
                                        container, IContentProvider.class);

                        contentProvider = clazz.newInstance();

                    } catch (Exception ex) {
                        LOG.error("Can not find contentProvider class '"
                                + contentProviderClassName + "'.", ex);

                        throw ex;
                    }
                }

                IModule module = (IModule) this.digester.peek();

                IHierarchicalFile f = declareFile(name, baseDirectory[0],
                        module, ds, container, contentProvider);

                String uri = getURI(name);
                filesByURI.put(uri, f);

                this.digester.push(f);
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                this.digester.pop();
            }
        });
    }

    protected URL searchContent(String name, String contentLocation,
            Object container) {
        URL url = null;

        if (container instanceof ClassLoader) {
            String cl = contentLocation;
            if (cl.startsWith("/")) {
                cl = cl.substring(1);
            }

            url = ((ClassLoader) container).getResource(cl);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get resource '" + cl + "' from classloader => "
                        + url);
            }
        }

        if (url == null && (container instanceof ServletContext)) {
            String cl = contentLocation;
            if (cl.startsWith("/") == false) {
                cl = "/" + cl;
            }

            try {
                url = ((ServletContext) container).getResource(cl);

            } catch (MalformedURLException e) {
                IllegalArgumentException ex = new IllegalArgumentException(
                        "Can not get resource '" + contentLocation
                                + "' into servlet context (file=" + name + ").");
                ex.initCause(e);

                throw ex;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get resource '" + cl + "' from classloader => "
                        + url);
            }
        }

        if (url == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Can not find resource ('" + name + "') '"
                        + contentLocation + "' from container='" + container
                        + "'.");
            }
        }

        return url;
    }

    public final IHierarchicalFile declareFile(String name, String directory,
            IModule module, IHierarchicalFile depends[], Object container,
            IContentProvider contentProvider) {
        final String contentLocation = getContentLocation(name, directory);

        URL url = searchContent(name, contentLocation, container);

        if (url == null) {
            throw new IllegalArgumentException("Can not locate file '" + name
                    + "'  (location='" + contentLocation + "')");
        }

        IContentRef contentRef = new URLContentRef(null, url);

        String rname = "f:" + name;
        IHierarchicalFile f = createFile(module, rname, name, name, contentRef,
                depends, contentProvider);

        IFile old = filesByName.put(name, f);
        if (old != null) {
            LOG.debug("Alreay defined ? " + old);
        }
        resourcesByName.put(rname, f);

        return f;
    }

    protected HierarchicalFile createFile(IModule module, String name,
            String filename, String unlocalizedURI,
            IContentRef unlocalizedContentLocation,
            IHierarchicalFile dependencies[], IContentProvider contentProvider) {

        return new HierarchicalFile(module, name, filename, unlocalizedURI,
                unlocalizedContentLocation, dependencies, contentProvider);
    }

    private String getContentLocation(String name, String directory) {
        if (directory == null) {
            return name;
        }
        return directory + name;
    }

    public IModule getModuleByName(String name) {
        return modulesByName.get(name);
    }

    public IHierarchicalFile getFileById(String id) {
        return (IHierarchicalFile) resourcesByName.get(id);
    }

    public ISet getSetByName(String name) {
        return setsByName.get(name);
    }

    private String getModuleFilename(String id) {
        return "vfm-" + id + ".js";
    }

    private String getURI(String name) {
        return name;
    }

    public IHierarchicalFile[] computeFiles(Collection<Object> files,
            int typeOfCollection, IContext context) {
        List<IHierarchicalFile> dependencies = null;
        List<IHierarchicalFile> deps = null;

        for (Iterator<Object> it = files.iterator(); it.hasNext();) {
            Object next = it.next();

            IHierarchicalFile file = convertType(next, typeOfCollection);

            if (file == null) {
                throw new IllegalArgumentException("Object '" + next
                        + "' can not be converted to file !");
            }

            if (context.contains(file)) {
                continue;
            }

            if (deps == null && dependencies != null) {
                deps = new ArrayList<IHierarchicalFile>(dependencies);
            }

            deps = computeFile(file, context, deps);

            if (deps == null
                    || deps.isEmpty()
                    || (dependencies != null && deps.size() == dependencies
                            .size())) {
                // La liste n'a pas chang�e !
                continue;
            }

            // La liste a chang�e !
            if (dependencies == null) {
                dependencies = new ArrayList<IHierarchicalFile>(
                        files.size() * 2);

            } else {
                deps.removeAll(dependencies); // Retire les doublons
            }

            dependencies.addAll(deps); // Ajoute les nouvelles dependances

            deps = null; // Reset la liste !
        }

        if (dependencies == null || dependencies.isEmpty()) {
            return HIERARCHICAL_FILE_EMPTY_ARRAY;
        }

        return dependencies.toArray(new IHierarchicalFile[dependencies.size()]);
    }

    protected IHierarchicalFile convertType(Object next, int typeOfCollection) {
        if (typeOfCollection == FILENAME_COLLECTION_TYPE) {
            String filename = (String) next;

            // On recherches les fichiers associ�s
            IHierarchicalFile file = (IHierarchicalFile) filesByName
                    .get(filename);
            if (file == null) {
                throw new IllegalArgumentException("File '" + filename
                        + "' is not known into repository !");
            }
            return file;
        }

        if (typeOfCollection == FILE_COLLECTION_TYPE) {
            return (IHierarchicalFile) next;
        }

        return null;
    }

    private List<IHierarchicalFile> computeFile(IHierarchicalFile file,
            IContext context, List<IHierarchicalFile> newFiles) {
        if (context.contains(file)) {
            return newFiles;
        }

        ISet set = null;
        IModule module = null;

        if (file instanceof ISet) {
            set = (ISet) file;
            file = null;

        } else if (file instanceof IModule) {
            module = (IModule) file;
            set = module.getSet();
            file = null;

        } else {
            module = file.getModule();
            set = module.getSet();
        }

        if (module != null && module.getGroupAllFiles()) {
            if (context.contains(module)) {
                // Il fait parti d'un module qui a été envoyé !
                return newFiles;
            }
            file = module;
        }

        if (set != null) {
            if (context.contains(set)) {
                // Il fait parti d'un set de modules qui a �t� envoy� !
                return newFiles;
            }
            file = set;
        }

        IHierarchicalFile ds[];

        if (file == set) {
            ds = set.listExternalDependencies();

        } else if (file == module) {
            ds = module.listExternalDependencies();

        } else {
            ds = file.listDependencies();
        }

        if (ds.length > 0) {
            for (int i = 0; i < ds.length; i++) {
                newFiles = computeFile(ds[i], context, newFiles);
            }

            if (set != null && context.contains(set)) {
                return newFiles;
            }

            // On retente, car il fait parti d'un module qui a �t� envoy� !
            if (context.contains(module)) {
                return newFiles;
            }
        }

        if (context.contains(file)) {
            return newFiles;
        }

        context.add(file);

        if (newFiles == null) {
            newFiles = new ArrayList<IHierarchicalFile>();
        }

        newFiles.add(file);

        return newFiles;
    }

    public IModule[] listModules() {
        Collection<IModule> c = modulesByName.values();

        return c.toArray(new IModule[c.size()]);
    }

    public ISet[] listSets() {
        Collection<ISet> c = setsByName.values();

        return c.toArray(new ISet[c.size()]);
    }

    public ISet declareSet(String name, String uri, String[] modules) {
        List<IModule> l = new ArrayList<IModule>(modules.length);

        for (int i = 0; i < modules.length; i++) {
            String moduleName = modules[i];

            IModule module = getModuleByName(moduleName);
            if (module == null) {
                throw new IllegalArgumentException(
                        "Can not find module '"
                                + moduleName
                                + "', please check the name or remove it from the web.xml !");
            }

            addModules(l, module);
        }

        IModule ms[] = l.toArray(new IModule[l.size()]);

        return declareSet(name, uri, ms);
    }

    public ISet declareSet(String name, String uri, IModule[] ms) {
        for (int i = 0; i < ms.length; i++) {
            ms[i].setGroupAllFiles(true);
        }

        String rname = "s:" + name;
        ISet set = new SetImpl(rname, name, uri, ms);

        filesByURI.put(uri, set);
        setsByName.put(name, set);
        resourcesByName.put(rname, set);

        return set;
    }

    private void addModules(List<IModule> modules, IModule module) {
        if (modules.contains(module)) {
            return;
        }

        if (module.getSet() != null) {
            return;
        }

        IModule extMods[] = module.listExternalModules();
        if (extMods != null && extMods.length > 0) {
            for (int i = 0; i < extMods.length; i++) {
                addModules(modules, extMods[i]);
            }
        }

        modules.add(module);
    }

    private static IModule[] filterModules(IModule[] modules) {
        List<IModule> l = new ArrayList<IModule>(modules.length);

        for (int i = 0; i < modules.length; i++) {
            IModule module = modules[i];

            if (module.getSet() != null) {
                continue;
            }

            l.add(module);
        }

        return l.toArray(new IModule[l.size()]);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    public class HierarchicalFile extends File implements IHierarchicalFile {

        private static final long serialVersionUID = -4635130019371035269L;

        private final IModule module;

        private final int hashCode;

        private IHierarchicalFile[] dependencies;

        public HierarchicalFile(IModule module, String name, String filename,
                String unlocalizedURI, IContentRef unlocalizedContentLocation,
                IHierarchicalFile[] dependencies,
                IContentProvider contentProvider) {
            super(name, filename, unlocalizedURI, unlocalizedContentLocation,
                    contentProvider);

            this.module = module;
            this.dependencies = dependencies;

            if (module != null) {
                ((Module) module).addFile(this);
            }

            int h = super.hashCode();
            if (module != null) {
                // ((Module) module).addFile(this);
                h ^= module.getFilename().hashCode();
            }

            this.hashCode = h;
        }

        public void addDependencies(IHierarchicalFile dependencies[]) {
            List<IHierarchicalFile> l = new ArrayList<IHierarchicalFile>(
                    Arrays.asList(this.dependencies));

            for (int i = 0; i < dependencies.length; i++) {
                IHierarchicalFile f = dependencies[i];
                if (l.contains(f)) {
                    continue;
                }

                l.add(f);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Dependencies of HFile[" + getId() + "]=" + l);
            }

            this.dependencies = l.toArray(new IHierarchicalFile[l.size()]);
        }

        public IHierarchicalFile[] listDependencies() {
            return dependencies;
        }

        public IModule getModule() {
            return module;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return "[HFile " + getId() + "]";
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    public class SetImpl extends HierarchicalFile implements ISet {

        private static final long serialVersionUID = -5572999892750207302L;

        private IHierarchicalFile externalDependencies[];

        private IHierarchicalFile dependencies[];

        public SetImpl(String id, String filename, String uri,
                IModule modules[]) {
            super(null, id, filename, uri, null, filterModules(modules), null);

            modules = (IModule[]) super.listDependencies();
            for (int i = 0; i < modules.length; i++) {
                if (modules[i].getSet() != null) {
                    throw new IllegalArgumentException("Module '"
                            + modules[i].getFilename()
                            + "' is already associated to a Set !");
                }

                ((Module) modules[i]).setSet(this);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Declare SET [" + id + "] => "
                        + Arrays.asList(modules));
            }
        }

        public IHierarchicalFile[] listExternalDependencies() {
            if (externalDependencies != null) {
                return externalDependencies;
            }

            Set<IHierarchicalFile> l = null;
            IHierarchicalFile files[] = listDependencies();

            for (int i = 0; i < files.length; i++) {
                IHierarchicalFile file = files[i];

                IHierarchicalFile dfiles[] = file.listDependencies();

                for (int j = 0; j < dfiles.length; j++) {
                    IHierarchicalFile dfile = dfiles[j];

                    IModule module = dfile.getModule();

                    if (module.getSet() == this) {
                        continue;
                    }

                    if (l == null) {
                        l = new HashSet<IHierarchicalFile>();
                    }

                    l.add(dfile);
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("ExternalDependencies of SET[" + getId() + "]=" + l);
            }

            if (l == null) {
                externalDependencies = HIERARCHICAL_FILE_EMPTY_ARRAY;

                return externalDependencies;
            }

            externalDependencies = l.toArray(new IHierarchicalFile[l.size()]);

            return externalDependencies;
        }

        @Override
        public IHierarchicalFile[] listDependencies() {
            if (dependencies != null) {
                return dependencies;
            }

            List<IHierarchicalFile> l = new ArrayList<IHierarchicalFile>();
            IModule modules[] = (IModule[]) super.listDependencies();
            for (int i = 0; i < modules.length; i++) {

                IHierarchicalFile fs[] = modules[i].listDependencies();

                l.addAll(Arrays.asList(fs));
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Dependencies of SET[" + getId() + "]=" + l);
            }

            dependencies = l.toArray(new IHierarchicalFile[l.size()]);

            return dependencies;
        }

        @Override
        public String toString() {
            return "[Set " + getId() + "]";
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    public class Module extends HierarchicalFile implements IModule {

        private static final long serialVersionUID = -5299468486306880225L;

        private boolean groupAllFiles;

        private List<IHierarchicalFile> filesList = new ArrayList<IHierarchicalFile>(
                8);

        private IHierarchicalFile files[];

        private ISet set;

        private IHierarchicalFile externalDependencies[];

        private IModule externalModules[];

        private boolean isDefaultCoreModule;

        public Module(String name, String filename, String uri,
                boolean groupAllFiles) {
            super(null, name, filename, uri, null, null, null);

            this.groupAllFiles = groupAllFiles;
        }

        public boolean isDefaultCoreModule() {
            return isDefaultCoreModule;
        }

        public void setDefaultCoreModule() {
            isDefaultCoreModule = true;
        }

        public void setGroupAllFiles(boolean enable) {
            this.groupAllFiles = enable;
        }

        public boolean getGroupAllFiles() {
            return groupAllFiles;
        }

        public void addFile(IHierarchicalFile file) {
            filesList.add(file);
        }

        @Override
        public IHierarchicalFile[] listDependencies() {
            if (files != null) {
                return files;
            }

            files = filesList.toArray(new IHierarchicalFile[filesList.size()]);
            filesList = null;

            return files;
        }

        public IHierarchicalFile[] listExternalDependencies() {
            if (externalDependencies != null) {
                return externalDependencies;
            }

            Set<IHierarchicalFile> l = null;
            IHierarchicalFile files[] = listDependencies();

            for (int i = 0; i < files.length; i++) {
                IHierarchicalFile file = files[i];

                IHierarchicalFile dfiles[] = file.listDependencies();

                for (int j = 0; j < dfiles.length; j++) {
                    IHierarchicalFile dfile = dfiles[j];

                    if (dfile.getModule() == this) {
                        continue;
                    }

                    if (l == null) {
                        l = new HashSet<IHierarchicalFile>();
                    }

                    l.add(dfile);
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("ExternalDependencies of MODULE[" + getId() + "]="
                        + l);
            }

            if (l == null) {
                externalDependencies = HIERARCHICAL_FILE_EMPTY_ARRAY;

                return externalDependencies;
            }

            externalDependencies = l.toArray(new IHierarchicalFile[l.size()]);

            return externalDependencies;
        }

        public IModule[] listExternalModules() {
            if (externalModules != null) {
                return externalModules;
            }

            Set<IModule> l = null;
            IHierarchicalFile files[] = listExternalDependencies();

            for (int i = 0; i < files.length; i++) {
                IModule module = files[i].getModule();
                if (module == null) {
                    continue;
                }

                if (l == null) {
                    l = new HashSet<IModule>();
                }

                l.add(module);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Dependencies of MODULE[" + getId() + "]=" + l);
            }

            if (l == null) {
                externalModules = MODULE_EMPTY_ARRAY;

                return externalModules;
            }

            externalModules = l.toArray(new IModule[l.size()]);

            return externalModules;
        }

        public void setSet(ISet set) {
            this.set = set;
        }

        public ISet getSet() {
            return set;
        }

        @Override
        public String toString() {
            return "[Module " + getId() + "]";
        }
    }
}
