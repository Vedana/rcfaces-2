/*
 * $Id: IAlertLoadingMessageCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Fred (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IAlertLoadingMessageCapability {

    /**
     * Returns a string value containing the message to show when the user attempts an action while loading
     * 
     * @return alertLoadingMessage string property
     */
    String getAlertLoadingMessage();

    /**
     * Sets a string value containing the message to show when the user attempts an action while loading
     * if not set, the default value is shown
     * if set to empty string, no message is shown
     * 
     * @param alertLoadingMessage
     *            alertLoadingMessage string property
     */
    void setAlertLoadingMessage(String alertLoadingMessage);
}
