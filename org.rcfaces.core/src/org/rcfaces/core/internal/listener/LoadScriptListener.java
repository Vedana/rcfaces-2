/*
 * $Id: LoadScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ILoadListener;
import org.rcfaces.core.event.LoadEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class LoadScriptListener extends AbstractScriptListener implements
        ILoadListener {
    

    public LoadScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public LoadScriptListener() {
    }

    public void processLoad(LoadEvent event) {
    }
}
