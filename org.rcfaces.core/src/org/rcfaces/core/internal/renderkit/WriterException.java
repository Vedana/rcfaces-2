/*
 * $Id: WriterException.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */

package org.rcfaces.core.internal.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class WriterException extends IOException {

    

    private static final long serialVersionUID = 473005368009076248L;

    private final UIComponent component;

    public WriterException(String message, Throwable throwable,
            UIComponent component) {
        super(message);

        initCause(throwable);

        this.component = component;
    }

    public UIComponent getUIComponent() {
        return component;
    }
}
