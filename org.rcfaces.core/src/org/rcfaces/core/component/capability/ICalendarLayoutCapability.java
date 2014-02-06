/*
 * $Id: ICalendarLayoutCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ICalendarLayoutCapability {

    int SHORT_LAYOUT = 1;

    int MEDIUM_LAYOUT = 2;

    int LONG_LAYOUT = 3;

    int FULL_LAYOUT = 4;

    int DEFAULT_LAYOUT = MEDIUM_LAYOUT;

    int getCalendarLayout();

    void setCalendarLayout(int layout);
}
