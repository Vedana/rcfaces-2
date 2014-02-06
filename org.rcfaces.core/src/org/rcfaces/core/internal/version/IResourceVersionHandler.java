/*
 * $Id: IResourceVersionHandler.java,v 1.1 2011/04/12 09:25:48 oeuillot Exp $
 */
package org.rcfaces.core.internal.version;

import java.net.URL;

import javax.faces.context.FacesContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:48 $
 */
public interface IResourceVersionHandler {

    String ID = "org.rcfaces.core.URL_REWRITING_PROVIDER";

    boolean isEnabled();

    String getResourceVersion(FacesContext facesContext, String absolutePath,
            URL contentURLIfKnown);
}
