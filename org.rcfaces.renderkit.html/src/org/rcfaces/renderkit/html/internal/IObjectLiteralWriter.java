/*
 * $Id: IObjectLiteralWriter.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public interface IObjectLiteralWriter {

	IJavaScriptWriter getParent();

	IJavaScriptWriter writeProperty(String propertyName) throws WriterException;

	IJavaScriptWriter writeSymbol(String symbol) throws WriterException;

	IJavaScriptWriter end() throws WriterException;
}
