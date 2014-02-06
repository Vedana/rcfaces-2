/*
 * $Id: CloseScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.CloseEvent;
import org.rcfaces.core.event.ICloseListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class CloseScriptListener extends AbstractScriptListener implements
        ICloseListener {
    

    public CloseScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public CloseScriptListener() {
    }

    public void processClose(CloseEvent event) {
    }
}
