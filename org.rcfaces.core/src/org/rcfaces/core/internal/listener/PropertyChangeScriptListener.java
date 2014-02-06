/*
 * $Id: PropertyChangeScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.IPropertyChangeListener;
import org.rcfaces.core.event.PropertyChangeEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class PropertyChangeScriptListener extends AbstractScriptListener
        implements IPropertyChangeListener {
    

    public PropertyChangeScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public PropertyChangeScriptListener() {
    }

    public void processPropertyChange(PropertyChangeEvent event) {
    }
}
