/*
 * $Id: SelectionScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ISelectionListener;
import org.rcfaces.core.event.SelectionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class SelectionScriptListener extends AbstractScriptListener implements
        ISelectionListener {
    

    public SelectionScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public SelectionScriptListener() {
    }

    public void componentSelected(SelectionEvent event) {
    }

}
