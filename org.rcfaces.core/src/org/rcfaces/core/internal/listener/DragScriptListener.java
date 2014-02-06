/*
 * $Id: DragScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.DragEvent;
import org.rcfaces.core.event.IDragListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class DragScriptListener extends AbstractScriptListener implements
        IDragListener {
    

    public DragScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public DragScriptListener() {
    }

    public void componentDragged(DragEvent event)
            throws AbortProcessingException {
    }
}
