/*
 * $Id: PreSelectionScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IPreSelectionListener;
import org.rcfaces.core.event.PreSelectionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class PreSelectionScriptListener extends AbstractScriptListener implements
        IPreSelectionListener {
    

    public PreSelectionScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public PreSelectionScriptListener() {
    }

	public void processPreSelection(PreSelectionEvent event) {
	}
}
