/*
 * $Id: IFiltredCollection.java,v 1.2 2013/01/11 15:46:58 jbmeslin Exp $
 */
package org.rcfaces.core.model;

import java.util.Iterator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:58 $
 */
public interface IFiltredCollection<T> {

    int NO_MAXIMUM_RESULT_NUMBER = -1;

	Iterator<T> iterator(IFilterProperties filterProperties,
            int maximumResultNumber);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
	 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:58 $
     */
	public interface IFiltredIterator<T> extends Iterator<T> {
        int getSize();

        void release();
    }
}
