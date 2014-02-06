/*
 * $Id: IFocusStyleClassCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IFocusStyleClassCapability {

    /**
     * Specify the style class of the component.
     * 
     * @param cssClass
     *            style class
     */
    void setFocusStyleClass(String cssClass);

    /**
     * Returns the style class of the component.
     * 
     * @return style class
     */
    String getFocusStyleClass();

}
