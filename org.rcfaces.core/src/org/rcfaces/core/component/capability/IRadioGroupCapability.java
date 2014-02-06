/*
 * $Id: IRadioGroupCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IRadioGroupCapability {

    /**
     * Returns a string specifying the name of the virtual entity that links
     * different components together.
     * 
     * @return group name
     */
    String getGroupName();

    /**
     * Sets a string specifying the name of the virtual entity that links
     * different components together.
     * 
     * @param groupName
     *            group name
     */
    void setGroupName(String groupName);
}