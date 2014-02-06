/*
 * $Id: ItemTools.java,v 1.3 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IResourceKeyParticipant;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.StateHolderTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:22 $
 */
public class ItemTools {

    private static final Log LOG = LogFactory.getLog(ItemTools.class);

    public static void restoreState(FacesContext facesContext, Object target,
            Object states) {
        restoreState(facesContext, target, null, states);
    }

    public static void restoreState(FacesContext facesContext, Object target,
            Class stopClass, Object states) {

        Map properties = StateHolderTools.loadMap(facesContext, states);

        Class cls = target.getClass();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(cls, stopClass);

        } catch (IntrospectionException e) {
            LOG.error("Can not inspect '" + target + "'.", e);

            throw new FacesException("Can not inspect '" + target + "'.", e);
        }

        PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];

            if (pd.getWriteMethod() == null) {
                continue;
            }

            String propertyName = pd.getName();
            if (properties.containsKey(propertyName) == false) {
                continue;
            }

            Object value = properties.get(propertyName);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Write property '" + propertyName + "' value '"
                        + value + "' to '" + target + "' => " + value);
            }

            try {
                pd.getWriteMethod().invoke(target, new Object[] { value });

            } catch (Exception ex) {
                LOG.error("Can not set property '" + propertyName + "' value='"
                        + value + "' to '" + target + "'.", ex);
            }
        }
    }

    public static Object saveState(FacesContext facesContext, Object target) {
        return saveState(facesContext, target, null);
    }

    public static Object saveState(FacesContext facesContext, Object target,
            Class stopClass) {
        Map properties = new HashMap();

        Class cls = target.getClass();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(cls, stopClass);

        } catch (IntrospectionException e) {
            LOG.error("Can not inspect '" + target + "'.", e);

            throw new FacesException("Can not inspect '" + target + "'.", e);
        }

        PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];

            if (pd.getReadMethod() == null) {
                continue;
            }

            String propertyName = pd.getName();

            Object value;
            try {
                value = pd.getReadMethod().invoke(target, (Object[]) null);

            } catch (Exception ex) {
                LOG.error("Can not get property '" + propertyName + "' from '"
                        + target + "'.", ex);
                continue;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get property '" + propertyName + "' from '" + target
                        + "' => " + value);
            }

            properties.put(propertyName, value);
        }

        if (properties.isEmpty()) {
            return null;
        }

        return StateHolderTools.saveMap(facesContext, properties);
    }

    public static void copyProperties(Object target, Object source) {
        copyProperties(target, null, source);
    }

    public static void copyProperties(Object target, Class stopClass,
            Object source) {
        Class targetClass = target.getClass();

        BeanInfo targetBeanInfo;
        try {
            targetBeanInfo = Introspector.getBeanInfo(targetClass);

        } catch (IntrospectionException e) {
            LOG.error("Can not inspect '" + target + "'.", e);

            throw new FacesException("Can not inspect '" + target + "'.", e);
        }

        PropertyDescriptor targetPds[] = targetBeanInfo
                .getPropertyDescriptors();
        Map targetPropertiesDescriptorByName = new HashMap(targetPds.length);
        for (int i = 0; i < targetPds.length; i++) {
            PropertyDescriptor pd = targetPds[i];
            if (pd.getWriteMethod() == null) {
                continue;
            }

            targetPropertiesDescriptorByName.put(pd.getName(), pd);
        }

        Class sourceClass = source.getClass();
        BeanInfo sourceBeanInfo;
        try {
            sourceBeanInfo = Introspector.getBeanInfo(sourceClass,
                    (stopClass != null) ? stopClass.getSuperclass() : null);

        } catch (IntrospectionException e) {
            LOG.error("Can not inspect '" + source + "'.", e);

            throw new FacesException("Can not inspect '" + source + "'.", e);
        }

        PropertyDescriptor sourcePds[] = sourceBeanInfo
                .getPropertyDescriptors();

        for (int i = 0; i < sourcePds.length; i++) {
            PropertyDescriptor sourcePd = sourcePds[i];

            if (sourcePd.getReadMethod() == null) {
                continue;
            }

            String propertyName = sourcePd.getName();

            PropertyDescriptor targetPd = (PropertyDescriptor) targetPropertiesDescriptorByName
                    .get(propertyName);
            if (targetPd == null) {
                continue;
            }

            Object value;
            try {
                value = sourcePd.getReadMethod()
                        .invoke(source, (Object[]) null);

            } catch (Exception ex) {
                LOG.error("Can not get property '" + propertyName + "' from '"
                        + target + "'.", ex);
                continue;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get property '" + propertyName + "' from '" + target
                        + "' => " + value);
            }

            if (value == null) {
                continue;
            }

            try {
                targetPd.getWriteMethod()
                        .invoke(target, new Object[] { value });

            } catch (Exception ex) {
                LOG.error("Can not set property '" + propertyName + "' value='"
                        + value + "' to '" + target + "'.", ex);
            }
        }
    }

    public static void participeKey(StringAppender sa, Map map) {
        if (map.isEmpty()) {
            return;
        }

        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Entry) it.next();

            String propertyName = (String) entry.getKey();
            Object value = entry.getValue();

            participeKey(sa, propertyName, value, map);
        }
    }

    public static void participeKey(StringAppender sa, Collection collection) {
        if (collection.isEmpty()) {
            return;
        }

        int i = 1;
        for (Iterator it = collection.iterator(); it.hasNext();) {
            String propertyName = "#" + (i++);
            Object value = it.next();

            participeKey(sa, propertyName, value, collection);
        }
    }

    public static void participeKey(StringAppender sa, Object object,
            Class stopClass) {
        Class cls = object.getClass();

        if (stopClass != null) {
            // On prend le parent !
            stopClass = stopClass.getSuperclass();
        }

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(cls, stopClass);

        } catch (IntrospectionException e) {
            LOG.error("Can not inspect '" + object + "'.", e);

            throw new FacesException("Can not inspect '" + object + "'.", e);
        }

        PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];

            if (pd.getReadMethod() == null) {
                continue;
            }

            String propertyName = pd.getName();

            Object value;
            try {
                value = pd.getReadMethod().invoke(object, (Object[]) null);

            } catch (Exception ex) {
                LOG.error("Can not get property '" + propertyName + "' from '"
                        + object + "'.", ex);
                continue;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Get property '" + propertyName + "' from '" + object
                        + "' => " + value);
            }

            participeKey(sa, propertyName, value, object);
        }
    }

    private static void participeKey(StringAppender sa, String propertyName,
            Object value, Object source) {

        sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR).append(
                propertyName);

        if (value == null) {
            return;
        }

        if (IResourceKeyParticipant.class.isInstance(value)) {
            participeKey(sa, value, null);
            return;
        }

        if ((value instanceof Number) || (value instanceof String)) {
            sa.append(String.valueOf(value));
            return;
        }

        if (value instanceof Map) {
            participeKey(sa, (Map) value);
            return;
        }

        if (value instanceof Collection) {
            participeKey(sa, (Collection) value);
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Unsupported type of value. property '" + propertyName
                    + "' from '" + source + "' => " + value);
        }

    }

}
