/*
 * $Id: IUserAgentVaryFileItem.java,v 1.1 2011/04/12 09:28:22 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.item;

import org.rcfaces.core.item.IFileItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:22 $
 */
public interface IUserAgentVaryFileItem extends IFileItem {
    String getUserAgent();
}
