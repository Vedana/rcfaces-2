/*
 * $Id: INamespaceSchema.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

import org.rcfaces.renderkit.html.internal.ns.NamespaceServlet.IBuffer;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:02 $
 */
public interface INamespaceSchema {
    String getName();

    String getNameSpace();

    String getSchemaLocation();

    IBuffer getBuffer(String resourceName);
}
