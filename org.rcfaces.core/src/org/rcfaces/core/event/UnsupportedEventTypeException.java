/*
 * $Id: UnsupportedEventTypeException.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.event;

import javax.faces.FacesException;
import javax.faces.event.FacesEvent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class UnsupportedEventTypeException extends FacesException {

    

    private static final long serialVersionUID = 267866609058774106L;

    private final FacesEvent event;

    public UnsupportedEventTypeException(FacesEvent event) {
        super("Event '" + event + "' is not supported by listener !");

        this.event = event;
    }

    public final FacesEvent getEvent() {
        return event;
    }

}
