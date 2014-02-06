/*
 * $Id: IHtmlComponentRenderContext.java,v 1.1 2011/04/12 09:28:10 oeuillot Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:28:10 $
 */
public interface IHtmlComponentRenderContext extends IComponentRenderContext {
    boolean hasClientDatas(boolean clear);

    IHtmlRenderContext getHtmlRenderContext();
}
