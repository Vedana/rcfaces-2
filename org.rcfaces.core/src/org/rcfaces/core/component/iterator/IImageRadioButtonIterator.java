/*
 * $Id: IImageRadioButtonIterator.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.iterator;

import java.util.NoSuchElementException;

import org.rcfaces.core.component.ImageRadioButtonComponent;

/**
 * An iterator over a collection of ImageRadioButtonComponents.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IImageRadioButtonIterator extends
        IComponentIterator<ImageRadioButtonComponent> {

    /**
     * Returns the next ImageRadioButtonComponent in the iteration. Calling this
     * method repeatedly until the {@link #hasNext()} method returns false will
     * return each element in the underlying collection exactly once.
     * 
     * @return the next ImageRadioButtonComponent in the iteration.
     * @exception NoSuchElementException
     *                iteration has no more ImageRadioButtonComponents.
     */
    ImageRadioButtonComponent next();

    /**
     * Returns an array containing all of the ImageRadioButtonComponents in this
     * list in proper sequence.
     * 
     * @return an array containing all of the ImageRadioButtonComponents in this
     *         list in proper sequence.
     */
    ImageRadioButtonComponent[] toArray();
}
