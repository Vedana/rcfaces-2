/*
 * $Id: IHiddenModeCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IHiddenModeCapability {

    /**
     * 
     */
    int SERVER_HIDDEN_MODE = 1;

    /**
     * 
     */
    int PHANTOM_HIDDEN_MODE = 2;

    /**
     * 
     */
    int IGNORE_HIDDEN_MODE = 4;

    /**
     * 
     */
    int DEFAULT_HIDDEN_MODE = IGNORE_HIDDEN_MODE;

    /**
     * 
     */
    int CLIENT_HIDDEN_MODE = IGNORE_HIDDEN_MODE;

    /**
     * Set the hidden mode in client side.
     * 
     * @param hiddenMode
     *            hidden mode
     */
    void setHiddenMode(int hiddenMode);

    /**
     * Returns the hidden mode in client side.
     * 
     * @return hidden mode
     */
    int getHiddenMode();
}
