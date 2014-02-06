/*
 * $Id: DropCompleteActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.DropCompleteEvent;
import org.rcfaces.core.event.IDropCompleteListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class DropCompleteActionListener extends AbstractActionListener
        implements IDropCompleteListener {
    

    private static final Class< ? >[] actionParameters = { DropCompleteEvent.class };

    public DropCompleteActionListener(String expression,
            boolean partialRendering) {
        super(expression, partialRendering);
    }

    public DropCompleteActionListener(String expression) {
        super(expression);
    }

    public DropCompleteActionListener() {
    }

    public void componentCompleteDropped(DropCompleteEvent event)
            throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }
}
