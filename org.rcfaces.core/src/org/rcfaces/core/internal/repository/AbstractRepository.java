/*
 * $Id: AbstractRepository.java,v 1.4 2013/11/13 12:53:24 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.IHierarchicalFile;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.IModule;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.ISet;
import org.rcfaces.core.internal.webapp.URIParameters;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
 */
public abstract class AbstractRepository implements IRepository {

    private static final long serialVersionUID = -3328226384749670660L;

    private static final Log LOG = LogFactory.getLog(AbstractRepository.class);

    protected static final IFile[] FILE_EMPTY_ARRAY = new IFile[0];

    private static final IContentRef[] CONTENT_REF_EMPTY_ARRAY = new IContentRef[0];

    protected final Map<String, IFile> filesByName = new HashMap<String, IFile>();

    protected final Map<String, IFile> filesByURI = new HashMap<String, IFile>();

    protected final String repositoryVersion;

    protected final String servletURI;

    public AbstractRepository(String servletURI, String repositoryVersion) {
        if (servletURI.length() > 0) {
            if (servletURI.endsWith("/") && servletURI.length() > 1) {
                servletURI = servletURI.substring(0, servletURI.length() - 1);

            } else if (servletURI.startsWith("/") == false) {
                servletURI = "/" + servletURI;
            }
        }

        this.servletURI = servletURI;
        this.repositoryVersion = repositoryVersion;
    }

    public final String getVersion() {
        return repositoryVersion;
    }

    public IFile getFileByURI(String uri) {
        return filesByURI.get(uri);
    }

    public IFile getFileByName(String name) {
        return filesByName.get(name);
    }

    public IContext createContext(ICriteria criteria) {
        ContextImpl contextImpl = new ContextImpl();

        contextImpl.setCriteria(criteria);

        return contextImpl;
    }

    protected abstract IContentProvider getDefaultContentProvider();

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    protected static class ContextImpl implements IContext {
        private final Set<IFile> files = new HashSet<IFile>(32);

        private ICriteria criteria;

        public ContextImpl() {
        }

        public void setCriteria(ICriteria criteria) {
            this.criteria = criteria;
        }

        public ICriteria getCriteria() {
            return criteria;
        }

        public boolean contains(IFile file) {
            return files.contains(file);
        }

        public boolean add(IFile file) {
            return files.add(file);
        }

        public IContext copy() {
            ContextImpl ctx = new ContextImpl();
            ctx.criteria = criteria;
            ctx.files.addAll(files);

            return ctx;
        }

        public void restoreState(FacesContext facesContext,
                IRepository repository, Object state) {
            if (state == null) {
                return;
            }

            Object fs[] = (Object[]) state;

            criteria = (ICriteria) UIComponentBase.restoreAttachedState(
                    facesContext, fs[0]);

            for (int i = 1; i < fs.length; i++) {
                IFile file = ((BasicHierarchicalRepository) repository)
                        .getFileById((String) fs[i]);

                if (file == null) {
                    continue;
                }

                add(file);
            }
        }

        public Object saveState(FacesContext facesContext) {
            if (files.isEmpty()) {
                return null;
            }

            Iterator<IFile> it = files.iterator();
            Object ret[] = new Object[files.size() + 1];
            ret[0] = UIComponentBase.saveAttachedState(facesContext, criteria);

            for (int i = 1; it.hasNext();) {
                IFile file = it.next();

                ret[i++] = ((File) file).getId();
            }

            return ret;
        }
    }

    protected ICriteria adaptCriteria(ICriteria criteria, IFile file) {
        return criteria;
    }

    protected FileByCriteria createFileByCriteria(IFile file,
            IContentProvider contentProvider, ICriteria criteria, String uri,
            IContentRef noCriteriaContentLocation) {
        return new FileByCriteria(file, contentProvider, criteria, uri,
                noCriteriaContentLocation);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    protected class File implements IFile {

        private static final long serialVersionUID = -8396517787887070898L;

        private final String id;

        private final String filename;

        private final IContentRef noCriteriaContentRef;

        private final String noCriteriaURI;

        private final IContentProvider contentProvider;

        private final int hashCode;

        private FileByCriteria noCriteriaFile;

        private Map<ICriteria, FileByCriteria> criteriaFiles;

        public File(String name, String filename, String noCriteriaURI,
                IContentRef noCriteriaContentRef,
                IContentProvider contentProvider) {
            this.id = name;
            this.filename = filename;
            this.noCriteriaURI = noCriteriaURI;
            this.noCriteriaContentRef = noCriteriaContentRef;
            this.contentProvider = contentProvider;
            this.hashCode = filename.hashCode();
        }

        public IRepository getRepository() {
            return AbstractRepository.this;
        }

        public final String getId() {
            return id;
        }

        public IContentRef[] getContentReferences(ICriteria criteria) {
            FileByCriteria localizedFile = getCriteriaFile(criteria);

            return localizedFile.getContentRefs();
        }

        public String getFilename() {
            return filename;
        }

        public IContentProvider getContentProvider() {
            if (contentProvider != null) {
                return contentProvider;
            }

            return computeContentProvider();
        }

        protected IContentProvider computeContentProvider() {
            return getDefaultContentProvider();
        }

        public String getURI(ICriteria criteria) {
            FileByCriteria criteriaFile = getCriteriaFile(criteria);

            return criteriaFile.getURI();
        }

        public ICriteria getSupportedCriteria(ICriteria criteria) {
            FileByCriteria criteriaFile = getCriteriaFile(criteria);
            if (criteriaFile == null) {
                return null;
            }

            return criteriaFile.getSelectedCriteria();
        }

        protected FileByCriteria getCriteriaFile(ICriteria criteria) {
            // On joue avec la synchro ... on bloque un minimum
            if (criteria == null) {
                if (noCriteriaFile != null) {
                    return noCriteriaFile;
                }

                noCriteriaFile = createFileByCriteria(this,
                        getContentProvider(), null, noCriteriaURI,
                        noCriteriaContentRef);
                return noCriteriaFile;
            }

            criteria = adaptCriteria(criteria, this);

            FileByCriteria criteriaFile;
            synchronized (this) {
                if (criteriaFiles == null) {
                    criteriaFiles = new HashMap<ICriteria, FileByCriteria>(4);
                }

                criteriaFile = criteriaFiles.get(criteria);
            }

            if (criteriaFile != null) {
                return criteriaFile;
            }

            criteriaFile = createFileByCriteria(this, getContentProvider(),
                    criteria, noCriteriaURI, noCriteriaContentRef);

            synchronized (this) {
                criteriaFiles.put(criteria, criteriaFile);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Save file='" + filename + "' criteria '" + criteria
                        + "' => " + criteriaFile);
            }

            return criteriaFile;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || (obj instanceof File) == false) {
                return false;
            }

            File f = (File) obj;

            return f.filename.equals(filename);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    protected ICriteria getDefaultRepositoryCriteria() {
        return null;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    protected class FileByCriteria {

        protected final IFile file;

        protected String uri;

        protected IContentRef[] contentRefs;

        protected ICriteria selectedCriteria;

        public FileByCriteria(IFile file, IContentProvider contentProvider,
                ICriteria criteria, String uri, IContentRef noCriteriaContentRef) {
            this.file = file;

            searchCriteriaSupport(contentProvider, criteria, uri,
                    noCriteriaContentRef);
        }

        protected void searchCriteriaSupport(IContentProvider contentProvider,
                ICriteria proposedCriteria, String uri,
                IContentRef noCriteriaContentRef) {

            ICriteria selectedCriteria = null;

            if ((file instanceof IModule) || (file instanceof ISet)) {
                IHierarchicalFile[] hfs = ((IHierarchicalFile) file)
                        .listDependencies();
                ICriteria childCriteria = null;

                for (IHierarchicalFile hf : hfs) {
                    FileByCriteria criteriaFile = ((AbstractRepository.File) hf)
                            .getCriteriaFile(proposedCriteria);
                    if (criteriaFile == null
                            || criteriaFile.getSelectedCriteria() == null) {
                        continue;
                    }

                    if (childCriteria == null) {
                        childCriteria = criteriaFile.getSelectedCriteria();

                    } else {
                        childCriteria = childCriteria.merge(criteriaFile
                                .getSelectedCriteria());
                    }
                }

                if (childCriteria != null) {
                    selectedCriteria = childCriteria;
                }

            } else {

                IContentRef[] criteriaContentRefs = null;

                if (proposedCriteria != null && noCriteriaContentRef != null) {
                    criteriaContentRefs = contentProvider
                            .searchCriteriaContentReference(
                                    noCriteriaContentRef, proposedCriteria);

                    if (criteriaContentRefs != null) {
                        selectedCriteria = proposedCriteria;

                    } else {
                        ICriteria defaultCriteria = getDefaultRepositoryCriteria();

                        if (defaultCriteria != null
                                && defaultCriteria.equals(proposedCriteria) == false) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Use default criteria: "
                                        + proposedCriteria);
                            }

                            criteriaContentRefs = contentProvider
                                    .searchCriteriaContentReference(
                                            noCriteriaContentRef,
                                            proposedCriteria);
                            if (criteriaContentRefs != null) {
                                selectedCriteria = defaultCriteria;
                            }
                        }
                    }

                    if (LOG.isTraceEnabled()) {
                        if (proposedCriteria != null
                                && criteriaContentRefs != null) {
                            LOG.trace("Find criteria version ("
                                    + proposedCriteria + ") of '" + uri
                                    + "' => " + criteriaContentRefs);

                        } else {
                            LOG.trace("Can not find criteria version ("
                                    + proposedCriteria + ") of '" + uri + "'.");
                        }
                    }
                }

                if (noCriteriaContentRef == null) {
                    contentRefs = CONTENT_REF_EMPTY_ARRAY;

                } else if (criteriaContentRefs == null) {
                    contentRefs = new IContentRef[] { noCriteriaContentRef };

                } else {
                    contentRefs = criteriaContentRefs;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Content locations of " + this + " => "
                            + Arrays.asList(contentRefs));
                }
            }

            if (selectedCriteria != null) {
                URIParameters uriParameters = URIParameters.parseURI(uri);

                selectedCriteria.appendSuffix(uriParameters);

                uri = uriParameters.computeParametredURI();
            }

            this.selectedCriteria = selectedCriteria;
            this.uri = uri;
        }

        public IContentRef[] getContentRefs() {
            return contentRefs;
        }

        public String getURI() {
            return uri;
        }

        public ICriteria getSelectedCriteria() {
            return selectedCriteria;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:24 $
     */
    public static abstract class AbstractContent implements IContent {

        public long getLastModified() throws IOException {
            return -1;
        }

        public long getLength() throws IOException {
            return -1;
        }

        public void release() {
        }

    }
}
