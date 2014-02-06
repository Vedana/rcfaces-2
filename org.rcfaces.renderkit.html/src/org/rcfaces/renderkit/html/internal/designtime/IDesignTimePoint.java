/*
 * $Id: IDesignTimePoint.java,v 1.1 2011/04/12 09:28:28 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.designtime;

import java.util.Locale;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:28 $
 */
public interface IDesignTimePoint {
    String getStyleSheetContent(Locale locale);

    String getJavaScriptContent(Locale locale);
}
