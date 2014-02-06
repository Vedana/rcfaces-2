/*
 * $Id: CheckListenerHandler.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.facelets;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.TagConfig;

import org.rcfaces.core.internal.taglib.CheckListenerTag;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public class CheckListenerHandler extends AbstractListenerHandler {
    

    public CheckListenerHandler(TagConfig config) {
        super(config);
    }

    protected void addListener(Object listener, UIComponent component) {
        CheckListenerTag.addCheckListener(listener, component);
    }

    protected String getListenerName() {
        return "check";
    }

}