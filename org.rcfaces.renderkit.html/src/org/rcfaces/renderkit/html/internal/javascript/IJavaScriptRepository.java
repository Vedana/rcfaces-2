/*
 * $Id: IJavaScriptRepository.java,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.javascript;

import java.util.Map;

import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.repository.IHierarchicalRepository;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */
public interface IJavaScriptRepository extends IHierarchicalRepository {

    int CLASS_NAME_COLLECTION_TYPE = 10;

    String getBaseURI(IProcessContext context);

    IClass getClassByName(String className);

    public interface IClassFile extends IHierarchicalFile {
        IClass[] listClasses();

        String convertSymbols(Map<String, String> symbols, String code);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
     */
    public interface IClass {
        String getName();

        IHierarchicalFile getFile();

        IClass[] listRequiredClasses(String requireId);

        IHierarchicalFile[] listRequiredResources(String requiredId);

        String getResourceBundleName();
    }
}
