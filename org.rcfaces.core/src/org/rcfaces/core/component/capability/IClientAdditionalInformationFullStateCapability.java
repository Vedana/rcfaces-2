/*
 * $Id: IClientAdditionalInformationFullStateCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IClientAdditionalInformationFullStateCapability extends
        IClientFullStateCapability {
    /**
     * Returns an integer value indicating whether the client should know about
     * the component's full state even if only a part of the data is present
     * (AJAX).
     * 
     * @return An integer value indicating whether the client should know about the component's full state. 
     * @see IClientFullStateCapability#NONE_CLIENT_FULL_STATE 
     * @see IClientFullStateCapability#ONEWAY_CLIENT_FULL_STATE 
     * @see IClientFullStateCapability#TWOWAYS_CLIENT_FULL_STATE 
     */
    int getClientAdditionalInformationFullState();

    /**
     * Sets an integer value indicating whether the client should know about the
     * component's full state even if only a part of the data is present (AJAX).
     * 
     * @param clientAdditionalInformationFullState
     * @see IClientFullStateCapability#NONE_CLIENT_FULL_STATE 
     * @see IClientFullStateCapability#ONEWAY_CLIENT_FULL_STATE 
     * @see IClientFullStateCapability#TWOWAYS_CLIENT_FULL_STATE 
     */
    void setClientAdditionalInformationFullState(
            int clientAdditionalInformationFullState);
}
