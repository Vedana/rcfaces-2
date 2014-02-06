/*
 * $Id: ErrorScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ErrorEvent;
import org.rcfaces.core.event.IErrorListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class ErrorScriptListener extends AbstractScriptListener implements
        IErrorListener {
    

    public ErrorScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public ErrorScriptListener() {
    }

    public void processError(ErrorEvent event) {
    }
}
