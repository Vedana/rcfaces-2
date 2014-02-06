/*
 * $Id: ISubInputClientIdRenderer.java,v 1.1 2011/04/12 09:28:10 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal;

import javax.faces.component.UIComponent;

import org.rcfaces.core.internal.renderkit.IRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:10 $
 */
public interface ISubInputClientIdRenderer {
    String computeSubInputClientId(IRenderContext renderContext,
            UIComponent component, String clientId);
}
