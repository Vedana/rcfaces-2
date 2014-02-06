/*
 * $Id: IServerDataCapability.java,v 1.2 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import java.util.Map;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IServerDataCapability {

    /**
     * Associates and object to a key for the component on the server side
     * 
     * @param name
     *            key used to retrieve the object associated
     * @param data
     *            object to associate
     * @return the object previously associated to teh key (null if none)
     */
    Object setServerData(String name, Object data);

    /**
     * Removes a key and the data object associated to it
     * 
     * @param name
     *            the key to remove
     * @return the data object that was associated to the key (null if none)
     */
    Object removeServerData(String name);

    /**
     * Retrieves an object data associated to a key
     * 
     * @param name
     *            the key to retrieve
     * @return the data object associated to the key
     */
    Object getServerData(String name);

    /**
     * Returns a list of the keys defined for the component
     * 
     * @return list of keys
     */
    String[] listServerDataKeys();

    /**
     * Returns the number of keys associated to the component.
     * 
     * @return number of keys
     */
    int getServerDataCount();

    /**
     * Returns a Map object containing keys and associated data objects
     * 
     * @return a map
     */
    Map<String, Object> getServerDataMap();
}
