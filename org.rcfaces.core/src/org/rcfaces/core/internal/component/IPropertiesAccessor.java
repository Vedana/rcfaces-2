/*
 * $Id: IPropertiesAccessor.java,v 1.1 2013/01/11 15:47:00 jbmeslin Exp $
 */
package org.rcfaces.core.internal.component;

import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IPropertiesAccessor {

    boolean isPropertySetted(String propertyName);

    Object getProperty(String propertyName);

    Object setProperty(FacesContext context, String propertyName, Object value);

    void setProperty(FacesContext context, String propertyName,
            ValueExpression value);

    Object removeProperty(FacesContext context, String name);

    void clearProperties(FacesContext context);

    Object saveState(FacesContext context);

    void restoreState(FacesContext context, Object state);

    void release();

    IDeltaPropertiesAccessor createDeltaPropertiesAccessor();

    Set<String> keySet();

    int size();

    void putAll(FacesContext context,
            Set<Map.Entry<String, Object>> propertiesMapEntry, Object undefined);

}