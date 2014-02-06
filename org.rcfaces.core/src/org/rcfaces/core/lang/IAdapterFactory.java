/*
 * $Id: IAdapterFactory.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

/**
 * <p>
 * An adapter factory defines behavioral extensions for one or more classes that
 * implements the IAdaptable interface. Adapter factories are registered with an
 * adapter manager.
 * </p>
 * 
 * Clients may implement this interface.
 * 
 * @author Eclipse Team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IAdapterFactory {
    /**
     * Returns an object which is an instance of the given class associated with
     * the given object. Returns <code>null</code> if no such object can be
     * found.
     * 
     * @param adaptableObject
     *            the adaptable object being queried (usually an instance of
     *            <code>IAdaptable</code>)
     * @param adapterType
     *            the type of adapter to look up
     * @return a object castable to the given adapter type, or <code>null</code>
     *         if this adapter factory does not have an adapter of the given
     *         type for the given object
     */

	<T> T getAdapter(Object adaptableObject, Class<T> adapterType,
            Object parameter);

    /**
     * Returns the collection of adapter types handled by this factory.
     * 
     * This method is generally used by an adapter manager to discover which
     * adapter types are supported, in advance of dispatching any actual
     * getAdapter requests.
     * 
     * @return the collection of adapter types
     */
	Class<?>[] getAdapterList();
}
