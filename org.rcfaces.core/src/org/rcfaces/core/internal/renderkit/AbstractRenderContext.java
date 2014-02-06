/*
 * $Id: AbstractRenderContext.java,v 1.3 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.NamingContainer;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.tools.BindingTools;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:26 $
 */
public abstract class AbstractRenderContext implements IRenderContext {

    private static final Log LOG = LogFactory
            .getLog(AbstractRenderContext.class);

    private static final boolean LOG_DEBUG = LOG.isDebugEnabled();

    private static final int COMPONENT_STACK_INITIAL_DEPTH = 16;

    private static final int SCOPE_VAR_INITIAL_SIZE = 4;

    private final List<Object> stack = new ArrayList<Object>(
            COMPONENT_STACK_INITIAL_DEPTH * 3);

    private List<VarScopeState> scopeVars = null;

    private FacesContext facesContext;

    private Map<String, Object> attributes;

    private boolean transientState;

    protected AbstractRenderContext() {
    }

    public final FacesContext getFacesContext() {
        return facesContext;
    }

    protected void initialize(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public String getComponentClientId(UIComponent component) {
        return component.getClientId(facesContext);
    }

    public String computeBrotherComponentClientId(UIComponent brotherComponent,
            String componentId) {

        if (componentId.length() > 0
                && componentId.charAt(0) == NamingContainer.SEPARATOR_CHAR) {
            return componentId;
        }

        String brotherClientId = getComponentClientId(brotherComponent);

        if (brotherClientId == null) {
            return null;
        }

        int idx = brotherClientId.lastIndexOf(NamingContainer.SEPARATOR_CHAR);
        if (idx < 0) {
            return componentId;
        }

        brotherClientId = brotherClientId.substring(0, idx + 1);

        return brotherClientId + componentId;
    }

    public void pushComponent(UIComponent component, String componentClientId) {
        stack.add(component);
        stack.add(componentClientId);
        stack.add(Boolean.FALSE);

        if (LOG_DEBUG) {
            StringBuffer sb = new StringBuffer();

            for (Iterator<Object> it = stack.iterator(); it.hasNext();) {
                it.next();
                sb.append(" / " + it.next());
                it.next();
            }

            LOG.debug("Push component. stack=" + sb);
        }
    }

    @SuppressWarnings("unchecked")
    public void popComponent(UIComponent component) {

        int level = getStackLevel();
        stack.remove(level);
        String componentClientId = (String) stack.remove(level);
        Object componentContextAttributes = stack.remove(level);
        if (componentContextAttributes != Boolean.FALSE) {
            releaseMap((Map<String, Object>) componentContextAttributes);
        }

        if (LOG_DEBUG) {
            StringBuffer sb = new StringBuffer();

            for (Iterator<Object> it = stack.iterator(); it.hasNext();) {
                it.next();
                sb.append(" / " + it.next());
                it.next();
            }

            LOG.debug("Pop component (" + componentClientId + "). stack=" + sb);
        }

        /*
         * On ne fait pas ca ... Car il y a peut etre d'autres composants
         * "freres" Camelia
         * 
         * if (getStackLevel() < 0) { release(); }
         */
    }

    /*
     * protected void releaseComponentAttributes(Map map) { map.clear(); }
     */

    private void releaseMap(Map< ? , ? > map) {
        map.clear();
    }

    protected int getStackLevel() {
        return stack.size() - 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rcfaces.core.internal.renderkit.IRenderContext#getComponentContext()
     */
    @SuppressWarnings("unchecked")
    public final Object getComponentContextAttribute(String key) {
        int componentContextLevel = getStackLevel() + 2;

        Object object = stack.get(componentContextLevel);
        if (object == Boolean.FALSE) {
            return null;
        }

        Map<String, Object> map = (Map<String, Object>) object;

        return map.get(key);
    }

    @SuppressWarnings("unchecked")
    public final boolean containsComponentContextAttribute(String key) {
        int componentContextLevel = getStackLevel() + 2;

        Object object = stack.get(componentContextLevel);
        if (object == Boolean.FALSE) {
            return false;
        }

        Map<String, Object> map = (Map<String, Object>) object;

        return map.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public final Object setComponentContextAttribute(String key, Object value) {
        int componentContextLevel = getStackLevel() + 2;

        Object object = stack.get(componentContextLevel);
        if (object == Boolean.FALSE) {
            object = new HashMap<String, Object>();
            stack.set(componentContextLevel, object);
        }

        Map<String, Object> map = (Map<String, Object>) object;

        return map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public final Object removeComponentContextAttribute(String key) {
        int componentContextLevel = getStackLevel() + 2;

        Object object = stack.get(componentContextLevel);
        if (object == Boolean.FALSE) {
            return null;
        }

        Map<String, Object> map = (Map<String, Object>) object;

        return map.remove(key);
    }

    public final Object getAttribute(String key) {
        if (attributes == null) {
            return null;
        }

        return attributes.get(key);
    }

    public final Object setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        return attributes.put(key, value);
    }

    public boolean containsAttribute(String key) {
        if (attributes == null) {
            return false;
        }
        return attributes.containsKey(key);
    }

    public Object removeAttribute(String key) {
        if (attributes == null) {
            return null;
        }

        return attributes.remove(key);
    }

    public final UIComponent getComponent() {

        int level = getStackLevel();

        if (level < 0) {
            throw new IllegalStateException("Empty stack of components !");
        }

        return (UIComponent) stack.get(level);
    }

    public final String getComponentClientId() {

        int level = getStackLevel();

        return (String) stack.get(level + 1);
    }

    public final IComponentWriter getComponentWriter() {
        return createWriter(getComponent());
    }

    protected abstract IComponentWriter createWriter(UIComponent component);

    public void encodeEnd(IComponentWriter writer) throws WriterException {
    }

    public void popScopeVar(String varName) {
        if (scopeVars == null || scopeVars.isEmpty()) {
            throw new FacesException("Scope var stack is empty");
        }

        int level = scopeVars.size();
        VarScopeState scope = scopeVars.remove(--level);

        if (varName.equals(scope.getVarName()) == false) {
            throw new FacesException("Not the same varName ? (stackVarName="
                    + scope.getVarName() + " varName=" + varName + ")");
        }

        getFacesContext().getExternalContext().getRequestMap().remove(varName);
    }

    public void pushScopeVar(String varName, Object scopeValue,
            Object valueBinding, boolean valueMustBeStored) {

        pushScopeVar(new VarScopeState(varName, scopeValue, valueBinding,
                valueMustBeStored));
    }

    protected void pushScopeVar(VarScopeState varScopeState) {

        FacesContext facesContext = getFacesContext();

        if (scopeVars == null) {
            scopeVars = new ArrayList<VarScopeState>(SCOPE_VAR_INITIAL_SIZE);
        }

        scopeVars.add(varScopeState);

        facesContext.getExternalContext().getRequestMap()
                .put(varScopeState.getVarName(), varScopeState.getValue());
    }

    public Object saveState(FacesContext facesContext) {
        if (scopeVars == null || scopeVars.isEmpty()) {
            return null;
        }

        Object sv[] = new Object[scopeVars.size()];

        int idx = 0;
        for (VarScopeState varScopeState : scopeVars) {

            sv[idx++] = varScopeState.saveState(facesContext);
        }

        return sv;
    }

    public void restoreState(FacesContext facesContext, Object object) {
        Object sv[] = (Object[]) object;
        if (sv == null) {
            return;
        }

        for (int i = 0; i < sv.length; i++) {
            VarScopeState varScopeState = new VarScopeState();

            varScopeState.restoreState(facesContext, sv[i]);

            pushScopeVar(varScopeState);
        }
    }

    public boolean isTransient() {
        return transientState;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientState = newTransientValue;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:26 $
     */
    public static class VarScopeState implements StateHolder {

        private String varName;

        private Object value;

        private Object valueBinding;

        private boolean valueMustBeStored;

        public VarScopeState() {
        }

        public VarScopeState(String varName, Object scopeValue,
                Object valueBinding, boolean valueMustBeStored) {
            this.varName = varName;
            this.value = scopeValue;
            this.valueBinding = valueBinding;
            this.valueMustBeStored = valueMustBeStored;
        }

        public void setTransient(boolean newTransientValue) {
        }

        public boolean isTransient() {
            return false;
        }

        public void restoreState(FacesContext facesContext, Object state) {
            Object ret[] = (Object[]) state;

            varName = (String) ret[0];

            valueMustBeStored = (ret[3] != null);

            valueBinding = UIComponentBase.restoreAttachedState(facesContext,
                    ret[2]);

            if (valueMustBeStored || valueBinding == null) {
                value = UIComponentBase.restoreAttachedState(facesContext,
                        ret[1]);

            } else if (valueBinding != null) {
                value = BindingTools.resolveBinding(facesContext, valueBinding);
            }
        }

        public Object saveState(FacesContext facesContext) {
            Object ret[] = new Object[4];

            ret[0] = varName;
            ret[3] = valueMustBeStored ? Boolean.TRUE : null;

            if (valueBinding != null) {
                ret[2] = UIComponentBase.saveAttachedState(facesContext,
                        valueBinding); // ValueBindig

                if (valueMustBeStored) {
                    ret[1] = UIComponentBase.saveAttachedState(facesContext,
                            value);
                }

            } else { // Pas de valueBinding, on enregistre la valeur
                ret[1] = UIComponentBase.saveAttachedState(facesContext, value); // Value
            }

            return ret;
        }

        public final String getVarName() {
            return varName;
        }

        public final Object getValue() {
            return value;
        }

        public final Object getValueBinding() {
            return valueBinding;
        }

        public final boolean isValueMustBeStored() {
            return valueMustBeStored;
        }
    }
}