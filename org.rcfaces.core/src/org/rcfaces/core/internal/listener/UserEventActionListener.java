/*
 * $Id: UserEventActionListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IUserEventListener;
import org.rcfaces.core.event.UserEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class UserEventActionListener extends AbstractActionListener implements
        IUserEventListener {
    

    private static final Class actionParameters[] = { UserEvent.class };

    public UserEventActionListener() {
        // Pour la déserialisation ...
    }

    public UserEventActionListener(String expression) {
        super(expression);
    }

    public UserEventActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.component.listener.IChangeListener#processChange(org
     * .rcfaces.core.component.listener.ChangeEvent)
     */
    public void processUserEvent(UserEvent event)
            throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }
}
