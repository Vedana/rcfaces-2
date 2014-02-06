/*
 * $Id: ClickActionListener.java,v 1.1 2013/01/11 15:47:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.ClickEvent;
import org.rcfaces.core.event.IClickListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:01 $
 */
public class ClickActionListener extends AbstractActionListener implements
        IClickListener {

    private static final Class< ? >[] actionParameters = { ClickEvent.class };

    public ClickActionListener() {
    }

    public ClickActionListener(String expression) {
        super(expression);
    }

    public ClickActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processClick(ClickEvent event) throws AbortProcessingException {
        process(event);
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }
}
