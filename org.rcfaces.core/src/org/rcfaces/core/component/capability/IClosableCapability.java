/*
 * $Id: IClosableCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * Experimental : Do not use !
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IClosableCapability {

    /**
     * Experimental : Do not use !
     * 
     * @return <code>true</code> is the component can be closed.
     */
    boolean isClosable();

    /**
     * Experimental : Do not use !
     * 
     * @param closable
     */
    void setClosable(boolean closable);
}
