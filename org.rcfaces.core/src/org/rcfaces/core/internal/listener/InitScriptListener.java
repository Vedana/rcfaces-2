/*
 * $Id: InitScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IInitListener;
import org.rcfaces.core.event.InitEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class InitScriptListener extends AbstractScriptListener implements
        IInitListener {
    

    public InitScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public InitScriptListener() {
    }

    public void processInit(InitEvent event) {
    }
}
