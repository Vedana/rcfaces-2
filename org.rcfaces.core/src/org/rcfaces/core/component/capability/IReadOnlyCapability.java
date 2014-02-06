/*
 * $Id: IReadOnlyCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IReadOnlyCapability {

    /**
     * Returns a boolean value indicating that this component will prohibit
     * changes by the user. The element may receive focus unless it has also
     * been disabled.
     * 
     * @return readOnly boolean property
     */
    boolean isReadOnly();

    /**
     * Sets a boolean value indicating that this component will prohibit changes
     * by the user. The element may receive focus unless it has also been
     * disabled.
     * 
     * @param readOnly
     *            readOnly boolean property
     */
    void setReadOnly(boolean readOnly);
}
