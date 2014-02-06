/*
 * $Id: AbstractComponentWriter.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public abstract class AbstractComponentWriter implements IComponentWriter {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractComponentWriter.class);

    private final IComponentRenderContext componentRenderContext;

    protected AbstractComponentWriter(
            IComponentRenderContext componentRenderContext) {
        this.componentRenderContext = componentRenderContext;
    }

    public IComponentRenderContext getComponentRenderContext() {
        return componentRenderContext;
    }
}
