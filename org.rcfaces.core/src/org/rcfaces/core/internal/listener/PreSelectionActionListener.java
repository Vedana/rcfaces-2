/*
 * $Id: PreSelectionActionListener.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IPreSelectionListener;
import org.rcfaces.core.event.PreSelectionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class PreSelectionActionListener extends AbstractActionListener
        implements IPreSelectionListener {

    private static final Class< ? >[] actionParameters = { PreSelectionEvent.class };

    public PreSelectionActionListener() {
    }

    public PreSelectionActionListener(String expression) {
        super(expression);
    }

    public PreSelectionActionListener(String expression,
            boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processPreSelection(PreSelectionEvent event)
            throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }

}
