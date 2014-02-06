/*
 * $Id: ICellToolTipTextCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ICellToolTipTextCapability {

    /**
     * Returns a string value containing the text that will appear when the
     * pointer hover the component.
     * 
     * @return default tool tip text
     */
    String getCellDefaultToolTipText();

    /**
     * Sets a string value containing the text that will appear when the pointer
     * hover the component.
     * 
     * @param cellDefaultToolTipText
     *            default tool tip text
     */
    void setCellDefaultToolTipText(String cellDefaultToolTipText);

    /**
     * Returns a string value containing the text that will appear when the
     * pointer hover the component.
     * 
     * @return tool tip text
     */
    String getCellToolTipText();

    /**
     * Sets a string value containing the text that will appear when the pointer
     * hover the component.
     * 
     * @param cellToolTipText
     *            tool tip text
     */
    void setCellToolTipText(String cellToolTipText);
}
