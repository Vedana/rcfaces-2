/*
 * $Id: AsyncHtmlOutputText.java,v 1.1 2014/01/03 16:24:30 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;

import org.rcfaces.core.internal.capability.IAsyncRenderComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/01/03 16:24:30 $
 */
public class AsyncHtmlOutputText extends HtmlOutputText {

	@Override
    public boolean isTransient() {
        if (super.isTransient() == false) {
            return false;
        }

        for (UIComponent parent = getParent(); parent != null; parent = parent
                .getParent()) {
            if (parent instanceof IAsyncRenderComponent) {

                setTransient(false);
                return false;
            }
        }

        return true;
    }
}
