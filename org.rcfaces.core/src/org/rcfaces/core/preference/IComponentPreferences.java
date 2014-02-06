/*
 * $Id: IComponentPreferences.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 * 
 */
package org.rcfaces.core.preference;

import java.io.Serializable;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IComponentPreferences extends StateHolder, Serializable {
    void loadPreferences(FacesContext facesContext, UIComponent component);

    void savePreferences(FacesContext facesContext, UIComponent component);
}
