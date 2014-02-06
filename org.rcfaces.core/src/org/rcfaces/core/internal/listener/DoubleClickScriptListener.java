/*
 * $Id: DoubleClickScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.DoubleClickEvent;
import org.rcfaces.core.event.IDoubleClickListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class DoubleClickScriptListener extends AbstractScriptListener implements
        IDoubleClickListener {
    

    public DoubleClickScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public DoubleClickScriptListener() {
    }

    public void processDoubleClick(DoubleClickEvent event) {
    }

}
