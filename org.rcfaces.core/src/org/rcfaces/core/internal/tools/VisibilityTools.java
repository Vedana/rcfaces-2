/*
 * $Id: VisibilityTools.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.tools;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public final class VisibilityTools {
    

    public static final boolean isVisible(UIComponent component) {
        for (; component != null; component = component.getParent()) {
            if (component.isRendered() == false) {
                return false;
            }
        }

        return true;
    }
}
