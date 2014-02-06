/*
 * $Id: IJavaScriptSymbolsConverter.java,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.javascript;

import java.util.Map;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */
public interface IJavaScriptSymbolsConverter {
    String SERVICE_ID = "org.rcfaces.javaScript.SYMBOLS_CONVERTER";

    String convertSymbols(String identifier, String code,
            Map<String, String> symbols,
            Map<String, Object> applicatioParameters);
}
