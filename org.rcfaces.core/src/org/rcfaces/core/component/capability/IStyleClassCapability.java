/*
 * $Id: IStyleClassCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A space-separated list of CSS style class(es) to be applied to the component.
 * This value will be passed through as the "class" attribute on generated
 * markup.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IStyleClassCapability {

    /**
     * Specify the style class of the component.
     * 
     * @param cssClass
     *            style class
     */
    void setStyleClass(String cssClass);

    /**
     * Returns the style class of the component.
     * 
     * @return style class
     */
    String getStyleClass();

}