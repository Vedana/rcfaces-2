/*
 * $Id: IRepository.java,v 1.2 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.webapp.URIParameters;
import org.rcfaces.core.lang.IAdaptable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
 */
public interface IRepository extends Serializable {

    String getVersion();

    IContext createContext(ICriteria criteria);

    IFile getFileByName(String filename);

    IFile getFileByURI(String uri);

    public interface ICriteria extends IAdaptable, StateHolder {
        void appendSuffix(URIParameters uriParameters);

        void appendSuffix(URIParameters uriParameters, boolean recursive);

        List<String> listURIs(String uri);

        ICriteria merge(ICriteria criteria);

        ICriteria getParent();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface IFile extends Serializable {
        IRepository getRepository();

        IContentProvider getContentProvider();

        String getFilename();

        IContentRef[] getContentReferences(ICriteria criteria);

        String getURI(ICriteria criteria);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface IContext {
        ICriteria getCriteria();

        boolean contains(IFile file);

        boolean add(IFile file);

        IContext copy();

        Object saveState(FacesContext facesContext);

        void restoreState(FacesContext facesContext, IRepository repository,
                Object state);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface IContentProvider {
        IContent getContent(IContentRef contentReference);

        IContentRef[] searchCriteriaContentReference(
                IContentRef contentReference, ICriteria proposedCriteria);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface IContent {
        InputStream getInputStream() throws IOException;

        long getLastModified() throws IOException;

        long getLength() throws IOException;

        void release();
    }
}
