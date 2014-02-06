/*
 * $Id: IDataMapAccessor.java,v 1.2 2013/11/26 13:55:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.component;

import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/26 13:55:57 $
 */
public interface IDataMapAccessor {

    Object getData(String name, FacesContext facesContext);

    Object setData(String name, Object data, FacesContext facesContext);
    
    ValueExpression getValueExpression(String key);

    void setData(String name, ValueExpression value, FacesContext facesContext);

    Object removeData(String name, FacesContext facesContext);

    int getDataCount();

    String[] listDataKeys(FacesContext facesContext);

    Map<String, Object> getDataMap(FacesContext facesContext);
}
