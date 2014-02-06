/*
 * $Id: AbstractProvider.java,v 1.2 2013/01/11 15:47:02 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.provider;

import javax.faces.context.FacesContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:02 $
 */
public abstract class AbstractProvider implements IProvider {
    private static final Log LOG = LogFactory.getLog(AbstractProvider.class);

    public void configureRules(Digester digester) {
    }

    public void startup(FacesContext facesContext) {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj instanceof AbstractProvider) == false) {
            return false;
        }

        return getId().equals(((AbstractProvider) obj).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "[AbstractProvider '" + getId() + "']";
    }
}
