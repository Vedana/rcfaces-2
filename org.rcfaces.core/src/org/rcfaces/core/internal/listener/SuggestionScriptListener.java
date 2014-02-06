/*
 * $Id: SuggestionScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ISuggestionListener;
import org.rcfaces.core.event.SuggestionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class SuggestionScriptListener extends AbstractScriptListener implements
        ISuggestionListener {
    

    public SuggestionScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public SuggestionScriptListener() {
    }

    public void processSuggestion(SuggestionEvent event) {
    }
}
