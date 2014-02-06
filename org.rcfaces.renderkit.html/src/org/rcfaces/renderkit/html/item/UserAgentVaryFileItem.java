/*
 * $Id: UserAgentVaryFileItem.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.item;

import javax.faces.component.UISelectItem;

import org.rcfaces.core.item.FileItem;
import org.rcfaces.core.item.IFileItem;
import org.rcfaces.renderkit.html.component.capability.IUserAgentVaryCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class UserAgentVaryFileItem extends FileItem implements
        IUserAgentVaryFileItem {
    

    private static final long serialVersionUID = 7658229482218017683L;

    private String userAgent;

    public UserAgentVaryFileItem() {
    }

    public UserAgentVaryFileItem(IFileItem fileItem) {
        super(fileItem);

        if (fileItem instanceof IUserAgentVaryFileItem) {
            userAgent = ((IUserAgentVaryFileItem) fileItem).getUserAgent();
        }
    }

    public UserAgentVaryFileItem(UISelectItem component) {
        super(component);

        if (component instanceof IUserAgentVaryCapability) {
            this.userAgent = ((IUserAgentVaryCapability) component)
                    .getUserAgent();
        }
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
