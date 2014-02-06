/*
 * $Id: UserEventScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IUserEventListener;
import org.rcfaces.core.event.UserEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class UserEventScriptListener extends AbstractScriptListener implements
        IUserEventListener {
    

    public UserEventScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public UserEventScriptListener() {
    }

    public void processUserEvent(UserEvent event) {
    }
}
