/*
 * $Id: KeyDownScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IKeyDownListener;
import org.rcfaces.core.event.KeyDownEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class KeyDownScriptListener extends AbstractScriptListener implements
        IKeyDownListener {
    

    public KeyDownScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public KeyDownScriptListener() {
    }

    public void processKeyDown(KeyDownEvent event) {
    }
}
