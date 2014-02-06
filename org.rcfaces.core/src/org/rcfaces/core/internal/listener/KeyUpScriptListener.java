/*
 * $Id: KeyUpScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IKeyUpListener;
import org.rcfaces.core.event.KeyUpEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class KeyUpScriptListener extends AbstractScriptListener implements
        IKeyUpListener {
    

    public KeyUpScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public KeyUpScriptListener() {
    }

    public void processKeyUp(KeyUpEvent event) {
    }
}
