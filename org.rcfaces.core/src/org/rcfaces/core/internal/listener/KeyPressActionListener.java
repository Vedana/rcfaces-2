/*
 * $Id: KeyPressActionListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IKeyPressListener;
import org.rcfaces.core.event.KeyPressEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class KeyPressActionListener extends AbstractActionListener implements
        IKeyPressListener {
    

    private static final Class actionParameters[] = { KeyPressEvent.class };

    public KeyPressActionListener() {
    }

    public KeyPressActionListener(String expression) {
        super(expression);
    }

    public KeyPressActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processKeyPress(KeyPressEvent event)
            throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }
}
