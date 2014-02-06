/*
 * $Id: IDefaultUnlockedPropertiesRenderer.java,v 1.2 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:02 $
 */
public interface IDefaultUnlockedPropertiesRenderer {
    Serializable[] getDefaultUnlockedProperties(FacesContext facesContext,
            UIComponent component);
}
