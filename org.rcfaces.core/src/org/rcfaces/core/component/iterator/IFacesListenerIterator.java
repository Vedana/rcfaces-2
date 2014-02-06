/*
 * $Id: IFacesListenerIterator.java,v 1.1 2011/04/12 09:25:44 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.iterator;

import java.util.NoSuchElementException;

import javax.faces.event.FacesListener;

/**
 * An iterator over a collection of FacesListeners.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:44 $
 */
public interface IFacesListenerIterator {
    /**
     * Returns the number of facesListeners in this list.
     * 
     * @return the number of facesListeners in this list.
     */
    int count();

    /**
     * Returns <tt>true</tt> if the iteration has more facesListeners. (In
     * other words, returns <tt>true</tt> if <tt>next</tt> would return an
     * element rather than throwing an exception.)
     * 
     * @return <tt>true</tt> if the iterator has more facesListeners.
     */
    boolean hasNext();

    /**
     * Returns the next FacesListener in the iteration. Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will return
     * each FacesListener in the underlying collection exactly once.
     * 
     * @return the next FacesListener in the iteration.
     * @exception NoSuchElementException
     *                iteration has no more FacesListeners.
     */
    FacesListener next();

    /**
     * Returns an array containing all of the FacesListeners in this list in
     * proper sequence.
     * 
     * @return an array containing all of the FacesListeners in this list in
     *         proper sequence.
     */
    FacesListener[] toArray();
}
