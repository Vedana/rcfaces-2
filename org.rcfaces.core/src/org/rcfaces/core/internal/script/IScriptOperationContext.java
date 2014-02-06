/*
 * $Id: IScriptOperationContext.java,v 1.1 2011/04/12 09:25:37 oeuillot Exp $
 */
package org.rcfaces.core.internal.script;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:37 $
 */
public interface IScriptOperationContext {
    String getCharset();

    void setCharset(String charset);

    long getLastModifiedDate();

    void setLastModifiedDate(long lastModifiedDate);

}
