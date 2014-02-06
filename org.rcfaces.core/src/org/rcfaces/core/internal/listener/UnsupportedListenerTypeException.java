/*
 * $Id: UnsupportedListenerTypeException.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.FacesException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class UnsupportedListenerTypeException extends FacesException {
    

    private static final long serialVersionUID = 2303989217005897745L;

    public UnsupportedListenerTypeException(String listenerType) {
        super("Listener of type '" + listenerType
                + "' defined in server side, is not supported !");
    }
}
