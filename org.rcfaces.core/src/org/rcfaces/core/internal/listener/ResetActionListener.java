/*
 * $Id: ResetActionListener.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IResetListener;
import org.rcfaces.core.event.ResetEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class ResetActionListener extends AbstractActionListener implements
        IResetListener {
    

    private static final Class actionParameters[] = { ResetEvent.class };

    public ResetActionListener() {
    }

    public ResetActionListener(String expression) {
        super(expression);
    }

    public ResetActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processReset(ResetEvent event) throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }

}
