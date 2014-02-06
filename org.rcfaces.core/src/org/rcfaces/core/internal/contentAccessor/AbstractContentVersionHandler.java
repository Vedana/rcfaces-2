/*
 * $Id: AbstractContentVersionHandler.java,v 1.4 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:23 $
 */
public abstract class AbstractContentVersionHandler implements
        IContentVersionHandler {
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null
                || (obj instanceof AbstractContentVersionHandler) == false) {
            return false;
        }

        return getId().equals(((AbstractContentVersionHandler) obj).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "[AbstractContentVersionHandler '" + getId() + "']";
    }

}
