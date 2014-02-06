/*
 * $Id: IPartialRenderingContext.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 */
package org.rcfaces.core.partialRendering;

import java.util.Map;

import javax.faces.component.UIComponent;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IPartialRenderingContext {
    void register(UIComponent component);

    void update(UIComponent component, String property, Map parameters);
}
