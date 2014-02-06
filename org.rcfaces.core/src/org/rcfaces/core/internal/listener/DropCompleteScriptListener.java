/*
 * $Id: DropCompleteScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.DropCompleteEvent;
import org.rcfaces.core.event.IDropCompleteListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class DropCompleteScriptListener extends AbstractScriptListener
        implements IDropCompleteListener {
    

    public DropCompleteScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public DropCompleteScriptListener() {
    }

    public void componentCompleteDropped(DropCompleteEvent event)
            throws AbortProcessingException {
    }
}
