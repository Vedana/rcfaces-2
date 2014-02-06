/*
 * $Id: IScopeColumnIdCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

public interface IScopeColumnIdCapability {
    /**
     * 
     * @return the identifier of the column
     */
    String getScopeColumnId();

    /**
     * 
     * @param columnId
     *            Identifier of column
     */
    void setScopeColumnId(String columnId);

}
