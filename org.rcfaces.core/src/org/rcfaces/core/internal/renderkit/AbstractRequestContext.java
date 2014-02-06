/*
 * $Id: AbstractRequestContext.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.renderkit;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.rcfaces.core.internal.AbstractReleasable;
import org.rcfaces.core.internal.Constants;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public abstract class AbstractRequestContext extends AbstractReleasable
        implements IRequestContext {
    

    protected static final String EMPTY_PROPERTIES[] = new String[0];

    private static final String LOCKED_CLIENT_ATTRIBUTES_PROPERTY = "org.rcfaces.core.LOCKED_CLIENT_ATTRIBUTES";

    private static final String LOCKED_CLIENT_ATTRIBUTES_ENABLED = LOCKED_CLIENT_ATTRIBUTES_PROPERTY;

    private final Map<String, Object> componentDatas = new HashMap<String, Object>(
            32);

    private IComponentData emptyComponentData;

    private FacesContext facesContext;

    private boolean lockedClientAttributes = Constants.LOCKED_CLIENT_ATTRIBUTES_DEFAULT_VALUE;

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
        this.componentDatas.clear(); // On ne sait jamais ....

        boolean configFound = false;

        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot != null) {
            Boolean enabled = (Boolean) viewRoot.getAttributes().get(
                    LOCKED_CLIENT_ATTRIBUTES_ENABLED);
            if (enabled != null) {
                lockedClientAttributes = enabled.booleanValue();
                configFound = true;
            }
        }

        if (configFound == false) {
            String enable = facesContext.getExternalContext().getInitParameter(
                    LOCKED_CLIENT_ATTRIBUTES_PROPERTY);
            if (enable != null) {
                lockedClientAttributes = Boolean.valueOf(enable).booleanValue();
                configFound = true;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.IRequestContext#getFacesContext()
     */
    public final FacesContext getFacesContext() {
        return facesContext;
    }

    protected final void putComponentData(String key, Object data) {
        componentDatas.put(key, data);
    }

    public boolean isLockedClientAttributes() {
        return lockedClientAttributes;
    }

    public static void setLockedAttributes(FacesContext facesContext,
            boolean lock) {

        facesContext.getViewRoot().getAttributes().put(
                LOCKED_CLIENT_ATTRIBUTES_ENABLED, Boolean.valueOf(lock));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.IRequestContext#getComponentData(
     * javax.faces.component.UIComponent)
     */
    public final IComponentData getComponentData(UIComponent component,
            String componentId, Renderer renderer) {
        Object data = componentDatas.get(componentId);

        if (data == null) {
            return emptyComponentData();
        }

        if (data instanceof IComponentData) {
            return (IComponentData) data;
        }
        
        data = getComponentData(component, componentId, data, renderer);
        if (data == null) {
            return emptyComponentData();
        }

        componentDatas.put(componentId, data);

        return (IComponentData) data;
    }

    protected IComponentData getComponentData(UIComponent component,
            String key, Object data, Renderer renderer) {
        return emptyComponentData();
    }

    protected IComponentData emptyComponentData() {
        if (emptyComponentData != null) {
            return emptyComponentData;
        }

        emptyComponentData = createEmptyComponentData();

        return emptyComponentData;
    }

    protected abstract IComponentData createEmptyComponentData();

    protected final String getKey(UIComponent component) {
        return component.getClientId(facesContext);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static abstract class AbstractComponentData extends
            AbstractProperties implements IComponentData {
        
    }

    public String getComponentId(UIComponent component) {
        return component.getClientId(getFacesContext());
    }

    @Override
    public void release() {
        componentDatas.clear();

        super.release();
    }

    /*
     * public void pushScopeVar(String var, ValueExpression valueBinding) {
     * Object value = valueBinding.getValue(getFacesContext());
     * 
     * facesContext.getExternalContext().getRequestMap().put(var, value); }
     */

}
