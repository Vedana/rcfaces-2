/*
 * $Id: ServiceEventActionListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IServiceEventListener;
import org.rcfaces.core.event.ServiceEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class ServiceEventActionListener extends
        AbstractReturnValueActionListener implements IServiceEventListener {
    

    private static final Class actionParameters[] = { ServiceEvent.class };

    public ServiceEventActionListener() {
    }

    public ServiceEventActionListener(String expression) {
        super(expression);
    }

    public ServiceEventActionListener(String expression,
            boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processServiceEvent(ServiceEvent event)
            throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }
}
