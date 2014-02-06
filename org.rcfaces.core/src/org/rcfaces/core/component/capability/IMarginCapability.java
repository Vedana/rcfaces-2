/*
 * $Id: IMarginCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IMarginCapability {

    /**
     * Returns a string value specifying the margin size for the component's top
     * (compliant to CSS margin-top).
     * 
     * @return margin-top
     */
    String getMarginTop();

    /**
     * Sets a string value specifying the margin size for the component's top
     * (compliant to CSS margin-top).
     * 
     * @param marginTop
     *            margin-top
     */
    void setMarginTop(String marginTop);

    /**
     * Returns a string value specifying the margin size for the component's
     * right (compliant to CSS margin-right).
     * 
     * @return margin-right
     */
    String getMarginRight();

    /**
     * Sets a string value specifying the margin size for the component's right
     * (compliant to CSS margin-right).
     * 
     * @param marginRight
     *            margin-right
     */
    void setMarginRight(String marginRight);

    /**
     * Returns a string value specifying the margin size for the component's
     * bottom (compliant to CSS margin-bottom).
     * 
     * @return margin-bottom
     */
    String getMarginBottom();

    /**
     * Sets a string value specifying the margin size for the component's bottom
     * (compliant to CSS margin-bottom).
     * 
     * @param marginBottom
     *            margin-bottom
     */
    void setMarginBottom(String marginBottom);

    /**
     * Returns a string value specifying the margin size for the component's
     * left (compliant to CSS margin-left).
     * 
     * @return margin-left
     */
    String getMarginLeft();

    /**
     * Sets a string value specifying the margin size for the component's left
     * (compliant to CSS margin-left).
     * 
     * @param marginLeft
     *            margin-left
     */
    void setMarginLeft(String marginLeft);
}
