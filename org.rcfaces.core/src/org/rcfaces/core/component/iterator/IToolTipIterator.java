/*
 * $Id: IToolTipIterator.java,v 1.1 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.component.iterator;

import java.util.NoSuchElementException;

import org.rcfaces.core.component.ToolTipComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IToolTipIterator extends IComponentIterator<ToolTipComponent> {

	/**
	 * Returns the next AdditionalInformationComponent in the iteration. Calling
	 * this method repeatedly until the {@link #hasNext()} method returns false
	 * will return each AdditionalInformationComponent in the underlying
	 * collection exactly once.
	 * 
	 * @return the next AdditionalInformationComponent in the iteration.
	 * @exception NoSuchElementException
	 *                iteration has no more AdditionalInformationComponent.
	 */
	ToolTipComponent next();

	/**
	 * Returns an array containing all of the AdditionalInformationComponent in
	 * this list in proper sequence.
	 * 
	 * @return an array containing all of the AdditionalInformationComponent in
	 *         this list in proper sequence.
	 */
	ToolTipComponent[] toArray();

}
