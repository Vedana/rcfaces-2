/*
 * $Id: IMaxResultNumberCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IMaxResultNumberCapability {

    /**
     * Sets an int value specifying the maximum number of rows for the result.
     * 
     * @param maximum
     *            max number of rows
     */
    void setMaxResultNumber(int maximum);

    /**
     * Returns an int value specifying the maximum number of rows for the
     * result.
     * 
     * @return max number of rows
     */
    int getMaxResultNumber();
}
