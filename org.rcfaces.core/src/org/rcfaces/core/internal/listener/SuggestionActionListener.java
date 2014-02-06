/*
 * $Id: SuggestionActionListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.ISuggestionListener;
import org.rcfaces.core.event.SuggestionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class SuggestionActionListener extends AbstractActionListener implements
        ISuggestionListener {
    

    private final static Class actionParameters[] = { SuggestionEvent.class };

    public SuggestionActionListener() {
        // Pour la déserialisation ...
    }

    public SuggestionActionListener(String expression) {
        super(expression);
    }

    public SuggestionActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.component.listener.IChangeListener#processChange(org
     * .rcfaces.core.component.listener.ChangeEvent)
     */
    public void processSuggestion(SuggestionEvent event)
            throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }
}
