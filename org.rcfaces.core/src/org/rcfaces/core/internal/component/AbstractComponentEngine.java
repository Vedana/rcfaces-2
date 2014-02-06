/*
 * $Id: AbstractComponentEngine.java,v 1.2 2013/12/11 10:17:38 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.rcfaces.core.internal.capability.IComponentEngine;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/12/11 10:17:38 $
 */
public abstract class AbstractComponentEngine implements IComponentEngine {

    static final Iterator< ? > EMPTY_ITERATOR = Collections.EMPTY_SET
            .iterator();

    protected final IFactory factory;

    AbstractComponentEngine(IFactory factory) {
        if (factory == null) {
            throw new NullPointerException("Factory is NULL !");
        }
        this.factory = factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponent#getFamily()
     */

    protected static final boolean isListEmpty(Collection< ? > list) {
        return (list == null) || (list.size() < 1);
    }

    protected static final boolean isMapEmpty(Map< ? , ? > map) {
        return (map == null) || (map.size() < 1);
    }

    @SuppressWarnings("unchecked")
    protected static final <T> Iterator<T> iteratorList(Collection<T> list) {
        if (isListEmpty(list)) {
            return (Iterator<T>) EMPTY_ITERATOR;
        }

        return list.iterator();
    }
}
