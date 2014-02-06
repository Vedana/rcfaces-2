/*
 * $Id: ValidationScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IValidationListener;
import org.rcfaces.core.event.ValidationEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class ValidationScriptListener extends AbstractScriptListener implements
        IValidationListener {
    

    public ValidationScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public ValidationScriptListener() {
    }

    public void processValidationEvent(ValidationEvent event) {
    }
}
