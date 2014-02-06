/*
 * $Id: IDesignerEngine.java,v 1.1 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit.designer;

import javax.faces.component.UIComponent;

import org.rcfaces.core.internal.renderkit.IComponentWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:02 $
 */
public interface IDesignerEngine {

    String MAIN_BODY = null;

    void beginChildren(UIComponent component, String facetName,
            IComponentWriter writer);

    void endChildren(UIComponent component, String facetName,
            IComponentWriter writer);

    void editableZone(UIComponent component, String propertyName,
            IComponentWriter writer);

    void declareCompositeChild(UIComponent component, UIComponent child);
}
