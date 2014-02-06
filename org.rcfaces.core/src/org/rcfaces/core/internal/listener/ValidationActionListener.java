/*
 * $Id: ValidationActionListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IValidationListener;
import org.rcfaces.core.event.ValidationEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class ValidationActionListener extends AbstractActionListener implements
        IValidationListener {
    

    private static final Class actionParameters[] = { ValidationEvent.class };

    public ValidationActionListener() {
        // Pour la déserialisation ...
    }

    public ValidationActionListener(String expression) {
        super(expression);
    }

    public ValidationActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.component.listener.IChangeListener#processChange(org
     * .rcfaces.core.component.listener.ChangeEvent)
     */
    public void processValidationEvent(ValidationEvent event)
            throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }
}
