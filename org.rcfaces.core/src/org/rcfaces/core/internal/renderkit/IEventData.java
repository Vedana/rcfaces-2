/*
 * $Id: IEventData.java,v 1.1 2011/04/12 09:25:41 oeuillot Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:41 $
 */
public interface IEventData {

    String getEventName();

    String getEventValue();

    String getEventItem();

    int getEventDetail();

    Object getEventObject(IDecoderContext decoderContext);
}
