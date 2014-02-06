/*
 * $Id: IClientDataCapability.java,v 1.2 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import java.util.Map;

/**
 * A tag used to associate data to a view.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IClientDataCapability {

    /**
     * Associates data to variable name.
     * 
     * @param name
     *            the variable to associate data to
     * @param data
     *            the data to associate to the variable
     * @return the data previously associated with the variable (empty if none)
     */
    String setClientData(String name, String data);

    /**
     * Removes the variable name and the data associated to it
     * 
     * @param name
     *            the variable to associate data to
     * @return the data that was associated to the variable
     */
    String removeClientData(String name);

    /**
     * Retrieves the data associated to a variable
     * 
     * @param name
     *            the variable to associate data to
     * @return the data associated to the variable
     */
    String getClientData(String name);

    /**
     * Returns the list of variable associated to the component.
     * 
     * @return a list of variables
     */
    String[] listClientDataKeys();

    /**
     * Returns the number of variable associated to the component.
     * 
     * @return number of variable
     */
    int getClientDataCount();

    /**
     * Returns a map containing the couples variable-data
     * 
     * @return a map
     */
    Map<String, String> getClientDataMap();
}
