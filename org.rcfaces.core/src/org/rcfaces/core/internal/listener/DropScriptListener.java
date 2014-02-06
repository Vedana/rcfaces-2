/*
 * $Id: DropScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.DropEvent;
import org.rcfaces.core.event.IDropListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class DropScriptListener extends AbstractScriptListener implements
        IDropListener {
    

    public DropScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public DropScriptListener() {
    }

    public void componentDropped(DropEvent event)
            throws AbortProcessingException {
    }
}
