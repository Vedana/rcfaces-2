/*
 * $Id: IForCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A component's id that express the link between the two components.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IForCapability {

    /**
     * Returns a component's id that express the link between the two
     * components.
     * 
     * @return id for a linked component
     */
    String getFor();

    /**
     * Sets a component's id that express the link between the two components.
     * 
     * @param forValue
     *            id for a linked component
     */
    void setFor(String forValue);
}
