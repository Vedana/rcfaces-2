/*
 * $Id: IComponentIterator.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.iterator;

import java.util.NoSuchElementException;

import javax.faces.component.UIComponent;

/**
 * An iterator over a collection of UIComponents.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IComponentIterator<T> {

    /**
     * Returns the number of components in this list.
     * 
     * @return the number of components in this list.
     */
    int count();

    /**
     * Returns <tt>true</tt> if the iteration has more components. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     * 
     * @return <tt>true</tt> if the iterator has more components.
     */
    boolean hasNext();

    /**
     * Returns the next component in the iteration. Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will return
     * each element in the underlying collection exactly once.
     * 
     * @return the next component in the iteration.
     * @exception NoSuchElementException
     *                iteration has no more component.
     */
    T nextComponent();

    /**
     * Returns an array containing all of the components in this list in proper
     * sequence; the runtime type of the returned array is that of the specified
     * array.
     * 
     * @param array
     *            the array into which the components of this list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose.
     * @return an array containing the components of this list.
     * 
     * @throws ArrayStoreException
     *             if the runtime type of the specified array is not a supertype
     *             of the runtime type of every component in this list.
     * @throws NullPointerException
     *             if the specified array is <tt>null</tt>.
     */
    UIComponent[] toArray(UIComponent[] array);
}
