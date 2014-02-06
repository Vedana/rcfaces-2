/*
 * $Id: MenuScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IMenuListener;
import org.rcfaces.core.event.MenuEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class MenuScriptListener extends AbstractScriptListener implements
        IMenuListener {
    

    public MenuScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public MenuScriptListener() {
    }

    public void menuShown(MenuEvent event) {
    }

}
