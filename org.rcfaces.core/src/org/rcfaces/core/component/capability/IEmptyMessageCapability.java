/*
 * $Id: IEmptyMessageCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A string containing the message shown when there is no result.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IEmptyMessageCapability {

    /**
     * Returns a string containing the message shown when there is no result.
     * 
     * @return message
     */
    String getEmptyMessage();

    /**
     * Sets the string conatining the message shown when there is no result.
     * 
     * @param emptyMessage
     *            the message
     */
    void setEmptyMessage(String emptyMessage);
}
