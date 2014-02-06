/*
 * $Id: StateHolderTools.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class StateHolderTools {

    private static final Set<String> PRIMITIVE_CLASSES = new HashSet<String>(12);
    static {
        PRIMITIVE_CLASSES.add(String.class.getName());
        PRIMITIVE_CLASSES.add(Long.class.getName());
        PRIMITIVE_CLASSES.add(Integer.class.getName());
        PRIMITIVE_CLASSES.add(Short.class.getName());
        PRIMITIVE_CLASSES.add(Byte.class.getName());
        PRIMITIVE_CLASSES.add(Boolean.class.getName());
        PRIMITIVE_CLASSES.add(Double.class.getName());
        PRIMITIVE_CLASSES.add(Float.class.getName());
        PRIMITIVE_CLASSES.add(Character.class.getName());
        PRIMITIVE_CLASSES.add(Date.class.getName());
        PRIMITIVE_CLASSES.add(Time.class.getName());
    }

    public static final boolean isPrimitive(Object value) {
        if (value == null) {
            return true;
        }

        return PRIMITIVE_CLASSES.contains(value.getClass().getName());
    }

    public static final Object saveMap(FacesContext facesContext,
            Map<String, Object> properties) {

        if (properties == null || properties.isEmpty()) {
            return null;
        }

        Object rets[] = new Object[properties.size() * 2];
        int i = 0;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {

            rets[i++] = entry.getKey();

            Object value = entry.getValue();

            if (StateHolderTools.isPrimitive(value) == false) {
                rets[i++] = UIComponentBase.saveAttachedState(facesContext,
                        value);

                continue;
            }

            rets[i++] = value;
        }

        return rets;
    }

    public static final Map<String, Object> loadMap(FacesContext facesContext,
            Object state) {

        Object datas[] = (Object[]) state;

        if (datas == null || datas.length == 0) {
            return new HashMap<String, Object>();
        }

        Map<String, Object> map = new HashMap<String, Object>(datas.length / 2);

        for (int i = 0; i < datas.length;) {
            String key = (String) datas[i++];
            Object value = datas[i++];

            if (StateHolderTools.isPrimitive(value) == false) {
                value = UIComponentBase.restoreAttachedState(facesContext,
                        value);
            }

            map.put(key, value);
        }

        return map;
    }
}
