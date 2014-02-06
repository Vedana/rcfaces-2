/*
 * $Id: SelectionActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.ISelectionListener;
import org.rcfaces.core.event.SelectionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class SelectionActionListener extends AbstractActionListener implements
        ISelectionListener {
    

    private static final Class< ? > actionParameters[] = { SelectionEvent.class };

    public SelectionActionListener() {
    }

    public SelectionActionListener(String expression) {
        super(expression);
    }

    public SelectionActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void componentSelected(SelectionEvent event)
            throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }

}
