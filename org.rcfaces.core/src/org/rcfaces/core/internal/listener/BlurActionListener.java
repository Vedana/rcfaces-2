/*
 * $Id: BlurActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.BlurEvent;
import org.rcfaces.core.event.IBlurListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class BlurActionListener extends AbstractActionListener implements
        IBlurListener {
    

    private static final Class< ? >[] actionParameters = { BlurEvent.class };

    public BlurActionListener() {
        // Pour la déserialisation ...
    }

    public BlurActionListener(String expression) {
        super(expression);
    }

    public BlurActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.component.listener.IChangeListener#processChange(org
     * .rcfaces.core.component.listener.ChangeEvent)
     */
    public void processBlur(BlurEvent event) throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }
}
