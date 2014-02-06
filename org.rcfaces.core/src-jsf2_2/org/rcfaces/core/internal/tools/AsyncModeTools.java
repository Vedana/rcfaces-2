/*
 * $Id: AsyncModeTools.java,v 1.1 2014/02/05 16:05:52 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import javax.faces.context.FacesContext;

public class AsyncModeTools {

    public static boolean isTagProcessor(Object object) {
        return true;
    }

    public static String getEnableValue(FacesContext context) {
        return null; // NEVER CALLED
    }

}
