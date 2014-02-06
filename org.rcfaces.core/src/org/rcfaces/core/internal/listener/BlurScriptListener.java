/*
 * $Id: BlurScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.BlurEvent;
import org.rcfaces.core.event.IBlurListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class BlurScriptListener extends AbstractScriptListener implements
        IBlurListener {
    

    public BlurScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public BlurScriptListener() {
    }

    public void processBlur(BlurEvent event) {
    }
}
