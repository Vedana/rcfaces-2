/*
 * $Id: FocusScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.FocusEvent;
import org.rcfaces.core.event.IFocusListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class FocusScriptListener extends AbstractScriptListener implements
        IFocusListener {
    

    public FocusScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public FocusScriptListener() {
    }

    public void processFocus(FocusEvent event) throws AbortProcessingException {
    }
}
