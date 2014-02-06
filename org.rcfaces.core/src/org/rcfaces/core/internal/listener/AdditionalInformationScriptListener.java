/*
 * $Id: AdditionalInformationScriptListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.AdditionalInformationEvent;
import org.rcfaces.core.event.IAdditionalInformationListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class AdditionalInformationScriptListener extends AbstractScriptListener
        implements IAdditionalInformationListener {
    

    public AdditionalInformationScriptListener(String scriptType, String command) {
        super(scriptType, command);
    }

    public AdditionalInformationScriptListener() {
    }

    public void processAdditionalInformation(AdditionalInformationEvent event)
            throws AbortProcessingException {
    }
}
