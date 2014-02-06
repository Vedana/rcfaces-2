/*
 * $Id: IEmptyDataMessageCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A string containing the message shown when there is no result.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IEmptyDataMessageCapability {

    /**
     * Returns a string containing the message shown when there is no result.
     * 
     * @return message
     */
    String getEmptyDataMessage();

    /**
     * Sets the string conatining the message shown when there is no result.
     * 
     * @param emptyDataMessage
     *            the message
     */
    void setEmptyDataMessage(String emptyDataMessage);
}
