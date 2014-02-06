/*
 * $Id: IAdaptable.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

/**
 * An interface for an adaptable object.
 * 
 * Adaptable objects can be dynamically extended to provide different interfaces
 * (or "adapters"). Adapters are created by adapter factories, which are in turn
 * managed by type by adapter managers. For example,
 * 
 * <pre>
 *     IAdaptable a = [some adaptable];
 *     IFoo x = (IFoo)a.getAdapter(IFoo.class);
 *     if (x != null)
 *        [do IFoo things with x]
 * </pre>
 * 
 * @author Eclipse team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IAdaptable {

    /**
     * Returns an object which is an instance of the given class associated with
     * this object. Returns <code>null</code> if no such object can be found.
     * 
     * @param adapter
     *            the adapter class to look up
     * @return a object castable to the given class, or <code>null</code> if
     *         this object does not have an adapter for the given class
     */
	<T> T getAdapter(Class<T> adapter, Object parameter);
}
