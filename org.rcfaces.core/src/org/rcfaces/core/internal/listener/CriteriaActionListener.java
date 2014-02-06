/*
 * $Id: CriteriaActionListener.java,v 1.2 2014/02/05 16:05:53 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.CriteriaEvent;
import org.rcfaces.core.event.ICriteriaListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2014/02/05 16:05:53 $
 */
public class CriteriaActionListener extends AbstractActionListener implements
		ICriteriaListener {

    private static final Class< ? >[] actionParameters = { CriteriaEvent.class };

	public CriteriaActionListener() {
		// Pour la déserialisation ...
	}

	public CriteriaActionListener(String expression) {
		super(expression);
	}

	public CriteriaActionListener(String expression, boolean partialRendering) {
		super(expression, partialRendering);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rcfaces.core.component.listener.IChangeListener#processChange(org
	 * .rcfaces.core.component.listener.ChangeEvent)
	 */
	public void processCriteriaChanged(CriteriaEvent event)
			throws AbortProcessingException {
		process(event);
	}

    @Override
    protected Class< ? >[] listParameterClasses() {
		return actionParameters;
	}
}
