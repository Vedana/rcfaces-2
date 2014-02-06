/*
 * $Id: ResetScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IResetListener;
import org.rcfaces.core.event.ResetEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class ResetScriptListener extends AbstractScriptListener implements
        IResetListener {
    

    public ResetScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public ResetScriptListener() {
    }

    public void processReset(ResetEvent event) {
    }

}
