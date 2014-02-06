/*
 * $Id: IRadioValueCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IRadioValueCapability extends IRadioGroupCapability {

    /**
     * Returns the object associated with the group valued for this component.
     * 
     * @return value object
     */
    Object getRadioValue();

    /**
     * Sets the object associated with the group valued for this component.
     * 
     * @param value
     *            value object
     */
    void setRadioValue(Object value);
}
