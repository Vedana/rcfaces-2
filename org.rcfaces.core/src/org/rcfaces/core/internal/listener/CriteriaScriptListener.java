/*
 * $Id: CriteriaScriptListener.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.CriteriaEvent;
import org.rcfaces.core.event.ICriteriaListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class CriteriaScriptListener extends AbstractScriptListener implements
		ICriteriaListener {

	public CriteriaScriptListener(String scriptType, String command) {
		super(scriptType, command);
	}

	public CriteriaScriptListener() {
	}

	public void processCriteriaChanged(CriteriaEvent event)
			throws AbortProcessingException {

	}
}
