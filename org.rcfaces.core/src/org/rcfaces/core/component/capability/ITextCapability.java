/*
 * $Id: ITextCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */

package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ITextCapability {

    /**
     * Sets a string value specifying the text to be shown.
     * 
     * @param text
     *            text
     */
    void setText(String text);

    /**
     * Returns a string value specifying the text shown.
     * 
     * @return text
     */
    String getText();
}
