/*
 * $Id: ServiceEventScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IServiceEventListener;
import org.rcfaces.core.event.ServiceEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class ServiceEventScriptListener extends AbstractScriptListener
        implements IServiceEventListener {
    

    public ServiceEventScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public ServiceEventScriptListener() {
    }

    public void processServiceEvent(ServiceEvent event) {
    }
}
