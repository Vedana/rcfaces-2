/*
 * $Id: ErrorListenerHandler.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.TagConfig;

import org.rcfaces.core.internal.taglib.ErrorListenerTag;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class ErrorListenerHandler extends AbstractListenerHandler {
    

    public ErrorListenerHandler(TagConfig config) {
        super(config);
    }

    @Override
    protected void addListener(Object listener, UIComponent component) {
        ErrorListenerTag.addErrorListener(listener, component);
    }

    @Override
    protected String getListenerName() {
        return "error";
    }

}