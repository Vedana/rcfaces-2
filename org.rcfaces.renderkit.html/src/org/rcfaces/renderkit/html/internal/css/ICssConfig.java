/*
 * $Id: ICssConfig.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.css;

import org.rcfaces.renderkit.html.internal.agent.IClientBrowser;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
public interface ICssConfig {

    String getDefaultStyleSheetURI();

    String getStyleSheetFileName(IClientBrowser clientBrowser);

    String getCharSet();

}
