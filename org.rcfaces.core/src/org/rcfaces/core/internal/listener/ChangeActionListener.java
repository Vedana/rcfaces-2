/*
 * $Id: ChangeActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class ChangeActionListener extends AbstractActionListener implements
        ValueChangeListener {
    

    private final static Class< ? > actionParameters[] = { ValueChangeEvent.class };

    public ChangeActionListener() {
    }

    public ChangeActionListener(String expression) {
        super(expression);
    }

    public ChangeActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processValueChange(ValueChangeEvent event)
            throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }
}
