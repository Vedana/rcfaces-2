/*
 * $Id: JavaScriptItem.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.item;

import javax.faces.component.UISelectItem;

import org.rcfaces.core.item.IFileItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class JavaScriptItem extends UserAgentVaryFileItem {
    

    private static final long serialVersionUID = -4358051605814119312L;

    public JavaScriptItem() {
    }

    public JavaScriptItem(IFileItem fileItem) {
        super(fileItem);
    }

    public JavaScriptItem(UISelectItem component) {
        super(component);
    }

}
