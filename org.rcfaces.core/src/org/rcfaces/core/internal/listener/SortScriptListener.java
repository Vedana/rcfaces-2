/*
 * $Id: SortScriptListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ISortListener;
import org.rcfaces.core.event.SortEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class SortScriptListener extends AbstractScriptListener implements
        ISortListener {
    

    public SortScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public SortScriptListener() {
    }

    public void processSort(SortEvent event) {
    }

}
