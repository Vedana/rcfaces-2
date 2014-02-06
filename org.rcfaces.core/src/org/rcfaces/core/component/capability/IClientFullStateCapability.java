/*
 * $Id: IClientFullStateCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IClientFullStateCapability {

    int NONE_CLIENT_FULL_STATE = 0;

    int ONEWAY_CLIENT_FULL_STATE = 1;

    int TWOWAYS_CLIENT_FULL_STATE = 2;

    int DEFAULT_CLIENT_FULL_STATE = NONE_CLIENT_FULL_STATE;

    /**
     * @deprecated Replaced by ONEWAY_CLIENT_FULL_STATE
     */
    int TRUE_CLIENT_FULL_STATE = ONEWAY_CLIENT_FULL_STATE;

    /**
     * @deprecated Replaced by NONE_CLIENT_FULL_STATE
     */
    int FALSE_CLIENT_FULL_STATE = NONE_CLIENT_FULL_STATE;
}
