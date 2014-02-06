/*
 * $Id: AbstractComponentPreferences.java,v 1.3 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.preference;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:25 $
 */
public abstract class AbstractComponentPreferences implements
        IComponentPreferences {

    private static final long serialVersionUID = -161366892815882804L;

    private transient boolean transientValue;

    public boolean isTransient() {
        return transientValue;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientValue = newTransientValue;
    }

}
