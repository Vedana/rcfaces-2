/*
 * $Id: IVerticalAlignmentCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * Vertical alignment.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IVerticalAlignmentCapability {

    /**
     * Returns vertical alignment.
     * 
     * @return vertical alignment
     */
    String getVerticalAlignment();

    /**
     * Specifies vertical alignment.
     * 
     * @param verticalAlignment
     *            vertical alignment
     */
    void setVerticalAlignment(String verticalAlignment);
}
