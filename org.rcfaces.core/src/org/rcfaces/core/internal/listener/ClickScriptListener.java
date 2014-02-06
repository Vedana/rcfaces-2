/*
 * $Id: ClickScriptListener.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ClickEvent;
import org.rcfaces.core.event.IClickListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class ClickScriptListener extends AbstractScriptListener implements
        IClickListener {

    public ClickScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public ClickScriptListener() {
    }

    public void processClick(ClickEvent event) {
    }

}
