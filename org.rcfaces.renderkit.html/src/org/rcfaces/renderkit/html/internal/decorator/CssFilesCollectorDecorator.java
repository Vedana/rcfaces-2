/*
 * $Id: CssFilesCollectorDecorator.java,v 1.1 2011/04/12 09:28:17 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.rcfaces.renderkit.html.component.CssStyleItemComponent;
import org.rcfaces.renderkit.html.item.CssStyleItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:17 $
 */
public class CssFilesCollectorDecorator extends
        UserAgentVaryFilesCollectorDecorator {

    public CssFilesCollectorDecorator(UIComponent component) {
        super(component);
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof CssStyleItemComponent) {
            return new CssStyleItem(component);
        }

        return super.createSelectItem(component);
    }

}
