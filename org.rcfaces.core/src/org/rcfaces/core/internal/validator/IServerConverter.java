/*
 * $Id: IServerConverter.java,v 1.1 2011/04/12 09:25:50 oeuillot Exp $
 */
package org.rcfaces.core.internal.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.validator.IParameter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:50 $
 */
public interface IServerConverter {

    String getId();

    String getClassName();

    IParameter[] listParameters();

    Converter getInstance(FacesContext facesContext, UIComponent component);
}
