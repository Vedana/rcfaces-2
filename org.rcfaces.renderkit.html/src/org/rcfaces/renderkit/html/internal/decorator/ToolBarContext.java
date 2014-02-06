/*
 * $Id: ToolBarContext.java,v 1.2 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
class ToolBarContext extends SelectItemsJsContext {
    

    public ToolBarContext(ISelectItemNodeWriter renderer,
            IComponentRenderContext componentRenderContext, Object value) {
        super(renderer, componentRenderContext, componentRenderContext
                .getComponent(), value);
    }
}
