/*
 * $Id: PropertyChangeActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.IPropertyChangeListener;
import org.rcfaces.core.event.PropertyChangeEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class PropertyChangeActionListener extends AbstractActionListener
        implements IPropertyChangeListener {
    

    private static final Class< ? >[] actionParameters = { PropertyChangeEvent.class };

    public PropertyChangeActionListener() {
    }

    public PropertyChangeActionListener(String expression) {
        super(expression);
    }

    public PropertyChangeActionListener(String expression,
            boolean partialRendering) {
        super(expression, partialRendering);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.component.listener.IChangeListener#processChange(org
     * .rcfaces.core.component.listener.ChangeEvent)
     */
    public void processPropertyChange(PropertyChangeEvent event)
            throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }
}
