/*
 * $Id: CheckScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.CheckEvent;
import org.rcfaces.core.event.ICheckListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class CheckScriptListener extends AbstractScriptListener implements
        ICheckListener {
    

    public CheckScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public CheckScriptListener() {
    }

    public void processCheck(CheckEvent event) {
    }
}
