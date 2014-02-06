/*
 * $Id: IComponentTimeZoneCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

import java.util.TimeZone;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IComponentTimeZoneCapability {

    /**
     * Returns the timeZone defined for the component.
     * 
     * @return the defined timeZone
     */
    TimeZone getComponentTimeZone();

    /**
     * Sets the timeZone for component.
     * 
     * @param timeZone
     *            timeZone to define
     */
    void setComponentTimeZone(TimeZone timeZone);
}
