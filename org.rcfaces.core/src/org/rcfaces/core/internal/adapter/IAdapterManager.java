/*
 * $Id: IAdapterManager.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.internal.adapter;

import org.rcfaces.core.lang.IAdapterFactory;

/**
 * 
 * @author Eclipse Team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IAdapterManager {

	<T> T getAdapter(Object adaptable, Class<T> adapterType, Object parameter);

	void registerAdapters(IAdapterFactory factory, Class<?> adaptable);
}
