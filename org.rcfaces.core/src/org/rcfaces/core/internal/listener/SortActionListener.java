/*
 * $Id: SortActionListener.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.ISortListener;
import org.rcfaces.core.event.SortEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class SortActionListener extends AbstractActionListener implements
        ISortListener {
    

    private static final Class actionParameters[] = { SortEvent.class };

    public SortActionListener() {
    }

    public SortActionListener(String expression) {
        super(expression);
    }

    public SortActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processSort(SortEvent event) throws AbortProcessingException {
        process(event);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }

}
