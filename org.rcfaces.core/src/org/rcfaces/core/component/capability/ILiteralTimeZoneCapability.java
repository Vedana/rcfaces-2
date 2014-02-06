/*
 * $Id: ILiteralTimeZoneCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

import java.util.TimeZone;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ILiteralTimeZoneCapability {

    /**
     * Returns the timeZone defined for literal attributes. (date)
     * 
     * @return the defined timeZone
     */
    TimeZone getLiteralTimeZone();

    /**
     * Sets the timeZone for literal attributes. (date)
     * 
     * @param timeZone
     *            timeZone to define
     */
    void setLiteralTimeZone(TimeZone timeZone);
}
