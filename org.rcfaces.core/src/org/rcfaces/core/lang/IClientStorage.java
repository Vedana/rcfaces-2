/*
 * $Id: IClientStorage.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

import java.util.Iterator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IClientStorage {
    Object getAttribute(String name);

    Object setAttribute(String name, Object value);

    Object removeAttribute(String name);

    boolean isEmpty();

    int getSize();

	Iterator<String> listAttributeNames();
}
