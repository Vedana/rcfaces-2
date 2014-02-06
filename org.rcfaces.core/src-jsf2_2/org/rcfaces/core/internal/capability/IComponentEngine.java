/*
 * $Id: IComponentEngine.java,v 1.1 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.capability;

import java.io.Serializable;

import javax.faces.component.StateHelper;
import javax.faces.context.FacesContext;

import org.rcfaces.core.internal.component.IDataMapAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2014/02/05 16:05:53 $
 */
public interface IComponentEngine {

    IComponentEngine copy();
    
    StateHelper getStateHelper();

    Object getProperty(Serializable attributeName, FacesContext facesContext);

    String getStringProperty(Serializable propertyName,
            FacesContext facesContext);

    void setProperty(Serializable propertyName, Object value);

    int getIntProperty(Serializable propertyName, int defaultValue,
            FacesContext facesContext);

    Object getLocalValue(Serializable propertyName);

    Object saveState(FacesContext currentInstance);

    IDataMapAccessor getDataMapAccessor(FacesContext context, String mapName,
            boolean create);

    void processDecodes(FacesContext context);

    void processValidation(FacesContext context);

    void processUpdates(FacesContext context);
}
