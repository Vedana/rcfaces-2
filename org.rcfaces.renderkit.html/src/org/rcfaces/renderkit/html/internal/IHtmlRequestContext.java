/*
 * $Id: IHtmlRequestContext.java,v 1.1 2011/04/12 09:28:10 oeuillot Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.internal.renderkit.IRequestContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:10 $
 */
public interface IHtmlRequestContext extends IRequestContext {
    IHtmlProcessContext getHtmlProcessContext();

    String getEventComponentId();
}
