/*
 * $Id: ErrorActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.ErrorEvent;
import org.rcfaces.core.event.IErrorListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class ErrorActionListener extends AbstractActionListener implements
        IErrorListener {
    

    private static final Class< ? >[] actionParameters = { ErrorEvent.class };

    public ErrorActionListener() {
    }

    public ErrorActionListener(String expression) {
        super(expression);
    }

    public ErrorActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processError(ErrorEvent event) throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }
}
