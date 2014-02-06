/*
 * $Id: IClientValidatorsRegistry.java,v 1.2 2013/11/13 12:53:26 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.validator;

import java.util.Locale;
import java.util.TimeZone;

import javax.faces.context.FacesContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:26 $
 */
public interface IClientValidatorsRegistry {
    IClientValidatorDescriptor getClientValidatorById(
            FacesContext facesContext, String clientValidatorId, Locale locale,
            TimeZone timeZone);
}
