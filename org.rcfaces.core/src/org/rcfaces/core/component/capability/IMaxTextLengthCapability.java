/*
 * $Id: IMaxTextLengthCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IMaxTextLengthCapability {

    /**
     * Returns an int value specifying the maximum number of characters that the
     * user can enter in the component.
     * 
     * @return maximum number of characters
     */
    int getMaxTextLength();

    /**
     * Sets an int value specifying the maximum number of characters that the
     * user can enter in the component.
     * 
     * @param maxTextLength
     *            maximum number of characters
     */
    void setMaxTextLength(int maxTextLength);
}
