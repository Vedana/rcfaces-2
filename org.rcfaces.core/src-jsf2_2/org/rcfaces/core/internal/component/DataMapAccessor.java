/*
 * $Id: DataMapAccessor.java,v 1.1 2014/02/05 16:05:52 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:52 $
 */
public class DataMapAccessor implements IDataMapAccessor {

    private static final String VALUE_EXPRESSION_PREFIX = ":RC:";

    private final UIComponent component;

    private final StateHelper stateHelper;

    private final String mapName;

    public DataMapAccessor(UIComponent component, StateHelper stateHelper,
            String mapName) {
        this.component = component;
        this.stateHelper = stateHelper;
        this.mapName = "rcfaces.dm:" + mapName;
    }

    public Object getData(String name, FacesContext facesContext) {
        Map<String, Object> map = getDataMap(facesContext);

        Object ret = map.get(name);

        if (ret != null) {
            return ret;
        }

        ValueExpression ve = (ValueExpression) map.get(VALUE_EXPRESSION_PREFIX
                + name);
        if (ve == null) {
            return null;
        }

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        ret = ve.getValue(facesContext.getELContext());

        return ret;
    }

    public Object setData(String name, Object value, FacesContext facesContext) {

        return stateHelper.put(mapName, name, value);
    }

    public void setData(String name, ValueExpression value,
            FacesContext facesContext) {

        removeData(name, facesContext);

        stateHelper.put(mapName, VALUE_EXPRESSION_PREFIX + name, value);
    }

    public Object removeData(String name, FacesContext facesContext) {
        return stateHelper.remove(mapName, name);
    }

    public int getDataCount() {
        Map<String, Object> map = getDataMap(null);
        if (map == null) {
            return 0;
        }

        return map.size();
    }

    public String[] listDataKeys(FacesContext facesContext) {
        Map<String, Object> map = getDataMap(null);
        if (map == null || map.isEmpty()) {
            return new String[0];
        }

        Collection keys = map.keySet();

        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public Map<String, Object> getDataMap(FacesContext facesContext) {
        Map<String, Object> map = (Map<String, Object>) stateHelper.get(mapName);
        if (map != null) {
            return map;
        }

        return Collections.emptyMap();
    }

	@Override
	public ValueExpression getValueExpression(String key) {
		return component.getValueExpression(key);
	}

}
