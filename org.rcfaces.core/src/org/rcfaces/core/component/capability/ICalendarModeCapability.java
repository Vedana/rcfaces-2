/*
 * $Id: ICalendarModeCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * A string specifying the selection mode :
 * <ul>
 * <li> date: one day per selection </li>
 * <li> period: a continuous period of time </li>
 * <li> periods: several non-continuous period of time (for example every
 * wednesday) </li>
 * </ul>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ICalendarModeCapability {
    /**
     * 
     */
    int DATE_CALENDAR_MODE = 0;

    /**
     * 
     */
    String DATE_CALENDAR_MODE_NAME = "date";

    /**
     * 
     */
    int PERIOD_CALENDAR_MODE = 1;

    /**
     * 
     */
    String PERIOD_CALENDAR_MODE_NAME = "period";

    /**
     * 
     */
    int DEFAULT_CALENDAR_MODE = DATE_CALENDAR_MODE;

    /**
     * 
     */
    String DEFAULT_CALENDAR_MODE_NAME = "default";

    /**
     * Returns a int value specifying the selection mode.
     * 
     * @return mode
     * @see #DATE_CALENDAR_MODE
     * @see #PERIOD_CALENDAR_MODE
     */
    int getMode();

    /**
     * Sets a int value specifying the selection mode.
     * 
     * @param mode
     * @see #DATE_CALENDAR_MODE
     * @see #PERIOD_CALENDAR_MODE
     */
    void setMode(int mode);
}
