/*
 * $Id: MouseOverScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IMouseOverListener;
import org.rcfaces.core.event.MouseOverEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class MouseOverScriptListener extends AbstractScriptListener implements
        IMouseOverListener {
    

    public MouseOverScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public MouseOverScriptListener() {
    }

    public void processMouseOver(MouseOverEvent event) {
    }
}
