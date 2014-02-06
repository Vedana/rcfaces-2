/*
 * $Id: IHierarchicalRepository.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.util.Collection;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public interface IHierarchicalRepository extends IRepository {

    int FILE_COLLECTION_TYPE = 0;

    int FILENAME_COLLECTION_TYPE = 1;

    IHierarchicalFile[] computeFiles(Collection<Object> collection,
            int typeOfCollection, IContext context);

    IModule[] listModules();

    IModule getModuleByName(String name);

    ISet[] listSets();

    ISet getBootSet();

    ISet getSetByName(String bootSet);

    void setBootSet(ISet set);

    ISet declareSet(String name, String uri, String[] moduleNames);

    ISet declareSet(String name, String uri, IModule[] modules);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface IHierarchicalFile extends IFile {

        IHierarchicalFile[] listDependencies();

        IModule getModule();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface ISet extends IHierarchicalFile {
        IHierarchicalFile[] listExternalDependencies();
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
     */
    public interface IModule extends IHierarchicalFile {
        boolean getGroupAllFiles();

        void setGroupAllFiles(boolean enable);

        boolean isDefaultCoreModule();

        void setDefaultCoreModule();

        IHierarchicalFile[] listExternalDependencies();

        IModule[] listExternalModules();

        ISet getSet();
    }
}
