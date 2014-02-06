/*
 * $Id: MouseOutScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IMouseOutListener;
import org.rcfaces.core.event.MouseOutEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class MouseOutScriptListener extends AbstractScriptListener implements
        IMouseOutListener {
    

    public MouseOutScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public MouseOutScriptListener() {
    }

    public void processMouseOut(MouseOutEvent event) {
    }

}
