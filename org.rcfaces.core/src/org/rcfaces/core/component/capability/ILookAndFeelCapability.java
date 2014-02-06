/*
 * $Id: ILookAndFeelCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */

package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ILookAndFeelCapability {

    /**
     * Sets a string value specifying the choosen look of the component.
     * 
     * @param ID
     *            lookId
     */
    void setLookId(String ID);

    /**
     * Returns a string value specifying the choosen look of the component.
     * 
     * @return lookId
     */
    String getLookId();
}
