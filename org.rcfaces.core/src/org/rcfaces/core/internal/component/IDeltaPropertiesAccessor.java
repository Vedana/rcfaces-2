/*
 * $Id: IDeltaPropertiesAccessor.java,v 1.1 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.component;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:00 $
 */
public interface IDeltaPropertiesAccessor extends IPropertiesAccessor {
    boolean hasModifiedProperties();

    void commitProperties(FacesContext facesContext);
}
