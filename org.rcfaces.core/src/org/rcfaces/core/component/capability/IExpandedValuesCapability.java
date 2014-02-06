/*
 * $Id: IExpandedValuesCapability.java,v 1.2 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

import javax.faces.context.FacesContext;

import org.rcfaces.core.lang.provider.IExpansionProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IExpandedValuesCapability extends IExpansionProvider {
    /*
     * Object getCheckedValues();
     * 
     * void setCheckedValues(Object values);
     */

    Class< ? > getExpandedValuesType(FacesContext facesContext);
}
