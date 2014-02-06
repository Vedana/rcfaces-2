/*
 * $Id: IApplicationExceptionCapability.java,v 1.1 2011/04/12 09:25:45 oeuillot Exp $
 */
package org.rcfaces.core.internal.service;

import org.rcfaces.core.lang.ApplicationException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:45 $
 */
public interface IApplicationExceptionCapability {
    ApplicationException getApplicationException();

    void setApplicationException(ApplicationException applicationException);
}
