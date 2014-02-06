/*
 * $Id: ISortComparatorCapability.java,v 1.2 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import java.util.Comparator;

/**
 * A string value specifying the "compare" function to use. it can reference a
 * javascript function or a keyword :
 * <ul>
 * <li> integer </li>
 * <li> number </li>
 * <li> alpha </li>
 * <li> alphaIgnoreCase </li>
 * <li> time </li>
 * <li> date </li>
 * <li> &lt;javascript function&gt; </li>
 * </ul>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:57 $
 */
public interface ISortComparatorCapability<T> {

    /**
     * Returns the sort Comparator object associated with the component
     * 
     * @return the sort Comparator object
     */
    Comparator<T> getSortComparator();

    /**
     * Associates a sort Comparator object to the component.
     * 
     * @param sortComparator
     *            the sort Comparator object
     */
    void setSortComparator(Comparator<T> sortComparator);
}
