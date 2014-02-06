/*
 * $Id: ChangeScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class ChangeScriptListener extends AbstractScriptListener implements
        ValueChangeListener {
    

    public ChangeScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public ChangeScriptListener() {
    }

    public void processValueChange(ValueChangeEvent event) {
    }
}
