/*
 * $Id: SelectItemsJsContext.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IDisabledCapability;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */

public class SelectItemsJsContext extends SelectItemsContext {
    

    private static final Log LOG = LogFactory
            .getLog(SelectItemsJsContext.class);

    private static final boolean LOG_VARS = LOG.isTraceEnabled();
    static {
        if (LOG_VARS) {
            LOG.info("LOG_VARS enabled !");
        }
    }

    private final List<String> varIds = new ArrayList<String>(8);

    private final boolean disabled;

    private String managerComponentId;

    public SelectItemsJsContext(ISelectItemNodeWriter renderer,
            IComponentRenderContext componentRenderContext,
            UIComponent rootComponent, Object value) {
        super(renderer, componentRenderContext, rootComponent, value);

        UIComponent component = getRootComponent();
        if (component instanceof IDisabledCapability) {
            disabled = ((IDisabledCapability) component).isDisabled();

        } else {
            disabled = false;
        }
    }

    public final String getManagerVarId() {
        return managerComponentId;
    }

    public final void setManagerComponentId(String managerComponentId) {
        this.managerComponentId = managerComponentId;
    }

    public void popVarId() {
        if (LOG_VARS) {
            LOG.trace("Pop var Id depth=" + varIds.size());
        }

        varIds.remove(varIds.size() - 1);
    }

    public void pushVarId(String varId) {
        varIds.add(varId);

        if (LOG_VARS) {
            LOG.trace("Push var Id=" + varId + " depth=" + varIds.size());
        }
    }

    public String peekVarId() {
        if (varIds.isEmpty()) {
            throw new NullPointerException("No var available into stack !");
        }
        return varIds.get(varIds.size() - 1);
    }

    public int countVarId() {
        return varIds.size();
    }

    public boolean isDisabled() {
        return disabled;
    }
}
