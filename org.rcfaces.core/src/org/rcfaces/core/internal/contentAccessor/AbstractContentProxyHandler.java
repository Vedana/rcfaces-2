/*
 * $Id: AbstractContentProxyHandler.java,v 1.4 2013/11/13 12:53:23 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:23 $
 */
public abstract class AbstractContentProxyHandler implements
        IContentProxyHandler {
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null
                || (obj instanceof AbstractContentProxyHandler) == false) {
            return false;
        }

        return getId().equals(((AbstractContentProxyHandler) obj).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "[AbstractContentProxyHandler '" + getId() + "']";
    }

}
