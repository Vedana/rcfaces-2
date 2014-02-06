/*
 * $Id: ICalendarDecoderRenderer.java,v 1.1 2011/04/12 09:28:09 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.util.Calendar;

import org.rcfaces.core.internal.renderkit.IDecoderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:09 $
 */
public interface ICalendarDecoderRenderer {

    Calendar getCalendar(IDecoderContext decoderContext, String attributeName);

}
