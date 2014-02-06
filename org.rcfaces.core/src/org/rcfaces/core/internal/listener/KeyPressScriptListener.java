/*
 * $Id: KeyPressScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IKeyPressListener;
import org.rcfaces.core.event.KeyPressEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class KeyPressScriptListener extends AbstractScriptListener implements
        IKeyPressListener {
    

    public KeyPressScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public KeyPressScriptListener() {
    }

    public void processKeyPress(KeyPressEvent event) {
    }
}
