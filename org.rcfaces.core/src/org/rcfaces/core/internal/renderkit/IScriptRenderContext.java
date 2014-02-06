/*
 * $Id: IScriptRenderContext.java,v 1.2 2013/11/13 12:53:26 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import java.util.Locale;

import javax.faces.component.StateHolder;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:26 $
 */
public interface IScriptRenderContext extends StateHolder {

    Locale getUserLocale();

//    String convertSymbol(String className, String memberName);
}
