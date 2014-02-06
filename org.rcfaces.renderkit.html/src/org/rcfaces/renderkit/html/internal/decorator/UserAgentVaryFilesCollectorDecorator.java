/*
 * $Id: UserAgentVaryFilesCollectorDecorator.java,v 1.1 2011/04/12 09:28:17 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

import org.rcfaces.renderkit.html.component.capability.IUserAgentVaryCapability;
import org.rcfaces.renderkit.html.item.UserAgentVaryFileItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:17 $
 */
public class UserAgentVaryFilesCollectorDecorator extends
        FilesCollectorDecorator {

    public UserAgentVaryFilesCollectorDecorator(UIComponent component) {
        super(component);
    }

    protected SelectItem createSelectItem(UISelectItem component) {
        if (component instanceof IUserAgentVaryCapability) {
            return new UserAgentVaryFileItem(component);
        }

        return super.createSelectItem(component);
    }

}
