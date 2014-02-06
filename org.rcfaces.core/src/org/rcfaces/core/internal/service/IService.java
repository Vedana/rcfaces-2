/*
 * $Id: IService.java,v 1.1 2011/04/12 09:25:45 oeuillot Exp $
 */
package org.rcfaces.core.internal.service;

import java.io.IOException;

import javax.faces.context.FacesContext;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:45 $
 */
public interface IService {

    void initialize(FacesContext facesContext);

    void service(FacesContext facesContext, String commandId)
            throws IOException;
}
