/*
 * $Id: AbstractSelectItemsRenderer.java,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
public abstract class AbstractSelectItemsRenderer extends AbstractCssRenderer {

    protected final boolean hasComponenDecoratorSupport() {
        return true;
    }

    public final boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component)
            throws IOException {

        // Les enfants ne doivent pas être rendus
    }
}