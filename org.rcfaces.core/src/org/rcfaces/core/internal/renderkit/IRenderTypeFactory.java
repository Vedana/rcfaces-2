/*
 * $Id: IRenderTypeFactory.java,v 1.1 2013/01/11 15:47:02 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:47:02 $
 */
public interface IRenderTypeFactory {

    String computeRendererType(FacesContext facesContext,
            UIComponent component, String componentFamily, String baseRendererType);

}
