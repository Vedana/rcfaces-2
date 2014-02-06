/*
 * $Id: ClientStorage.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.lang;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;

import org.rcfaces.core.lang.IClientStorage;
import org.rcfaces.core.lang.Time;
import org.w3c.dom.Document;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public class ClientStorage implements IClientStorage, Serializable {
    

    private static final long serialVersionUID = -4206007717671659424L;

    private static final Iterator<String> EMPTY_ITERATOR = Collections
            .<String> emptyList().iterator();

    private Map<String, Object> values = null;

    // private transient boolean modified = false;

    public ClientStorage() {
    }

    public synchronized Object getAttribute(String name) {
        if (values == null) {
            return null;
        }

        return values.get(name);
    }

    public synchronized Object setAttribute(String name, Object value) {
        verifyValue(value);

        if (values == null) {
            values = new HashMap<String, Object>();
        }

        String ret = (String) values.put(name, value);

        /*
         * if (value != ret) { if (value == null || value.equals(ret) == false) {
         * modified = true; } }
         */

        return ret;
    }

    public synchronized Object removeAttribute(String name) {
        if (values == null) {
            return null;
        }

        String ret = (String) values.remove(name);

        /*
         * if (ret != null) { modified = true; }
         */

        if (values.isEmpty()) {
            values = null;
        }

        return ret;
    }

    public synchronized Iterator<String> listAttributeNames() {
        if (values == null) {
            return EMPTY_ITERATOR;
        }

        return values.keySet().iterator();
    }

    public synchronized int getSize() {
        if (values == null) {
            return 0;
        }

        return values.size();
    }

    public synchronized boolean isEmpty() {
        if (values == null) {
            return true;
        }

        return values.isEmpty();
    }

    public Map<String, Object> getInternalMap() {
        return values;
    }

    private static void verifyValue(Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof String) {
            return;
        }
        if (value instanceof Date) {
            return;
        }
        if (value instanceof Time) {
            return;
        }
        if (value instanceof Number) {
            return;
        }
        if (value instanceof Document) {
            return;
        }

        throw new FacesException("Invalid object type '" + value.getClass()
                + "' for a clientStorage value.");
    }

    /*
     * public boolean isModified() { return modified; }
     */
}
