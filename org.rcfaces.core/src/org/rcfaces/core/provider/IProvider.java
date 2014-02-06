/*
 * $Id: IProvider.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 * 
 */
package org.rcfaces.core.provider;

import javax.faces.context.FacesContext;

import org.apache.commons.digester.Digester;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IProvider {

    String getId();

    void configureRules(Digester digester);

    void startup(FacesContext facesContext);
}
