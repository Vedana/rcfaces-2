/*
 * $Id: ComponentEngine.java,v 1.1 2014/02/05 16:05:52 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import java.io.Serializable;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.ComponentStateHelper0;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.capability.IComponentEngine;
import org.rcfaces.core.internal.capability.IValueExpressionCapability;

public class ComponentEngine extends ComponentStateHelper0 implements
        IComponentEngine {

    public ComponentEngine(UIComponent component) {
        super(component);
    }

    public Object getProperty(Serializable attributeName,
            FacesContext facesContext) {
        return eval(attributeName);
    }

    public String getStringProperty(Serializable propertyName,
            FacesContext facesContext) {
        Object value = getProperty(propertyName, facesContext);
        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    public int getIntProperty(Serializable propertyName, int defaultValue,
            FacesContext facesContext) {
        Object Value = getProperty(propertyName, facesContext);
        if ((Value instanceof Number) == false) {
            return defaultValue;
        }

        return ((Number) Value).intValue();
    }

    public void setProperty(Serializable propertyName, Object value) {
        put(propertyName, value);
    }

    public Object getLocalValue(Serializable propertyName) {
        return get(propertyName);
    }

    public IDataMapAccessor getDataMapAccessor(FacesContext context,
            String mapName, boolean create) {
        if (create == false && get(mapName) == null) {
            return null;
        }

        return new DataMapAccessor(component, this, mapName);
    }

    public StateHelper getStateHelper() {
        return this;
    }

    public void processDecodes(FacesContext context) {
        // TODO Auto-generated method stub

    }

    public void processValidation(FacesContext context) {
        // TODO Auto-generated method stub

    }

    public IComponentEngine copy() {
        ComponentEngine copy=new ComponentEngine(component);
        
        copy.defaultMap.putAll(defaultMap);
        // TODO : pas la delta ?  
        return copy;
    }

    public void processUpdates(FacesContext context) {
        if (deltaMap == null || deltaMap.isEmpty()) {
            return;
        }

        IValueExpressionCapability valueExpressionCapability = (IValueExpressionCapability) component;

        if (context == null) {
            context = FacesContext.getCurrentInstance();
        }
        ELContext elContext = context.getELContext();

        for (Map.Entry<Serializable, Object> entry : deltaMap.entrySet()) {
            Serializable propertyName = entry.getKey();
            Object propertyValue = entry.getValue();

            ValueExpression valueExpression = valueExpressionCapability
                    .getValueExpression(propertyName);
            if (valueExpression == null) {
                continue;
            }

            valueExpression.setValue(elContext, propertyValue);
        }
    }

}
