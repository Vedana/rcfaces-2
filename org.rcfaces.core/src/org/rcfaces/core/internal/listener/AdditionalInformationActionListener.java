/*
 * $Id: AdditionalInformationActionListener.java,v 1.3 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.listener;

import javax.faces.event.AbortProcessingException;

import org.rcfaces.core.event.AdditionalInformationEvent;
import org.rcfaces.core.event.IAdditionalInformationListener;
import org.rcfaces.core.event.SelectionEvent;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:05 $
 */
public class AdditionalInformationActionListener extends AbstractActionListener
        implements IAdditionalInformationListener {
    

    private static final Class< ? >[] actionParameters = { SelectionEvent.class };

    public AdditionalInformationActionListener() {
    }

    public AdditionalInformationActionListener(String expression) {
        super(expression);
    }

    public AdditionalInformationActionListener(String expression,
            boolean partialRendering) {
        super(expression, partialRendering);
    }

    public void processAdditionalInformation(AdditionalInformationEvent event)
            throws AbortProcessingException {
    }

    @Override
    protected Class< ? >[] listParameterClasses() {
        return actionParameters;
    }

}
