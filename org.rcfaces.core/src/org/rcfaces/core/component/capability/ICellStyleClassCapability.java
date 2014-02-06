/*
 * $Id: ICellStyleClassCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface ICellStyleClassCapability {

    /**
     * Returns a space-separated list of default CSS style class(es) to be
     * applied when this cell is rendered. This value will be passed through as
     * the "class" attribute on generated markup.
     * 
     * @return list of default CSS style classes
     */
    String getDefaultCellStyleClass();

    /**
     * Sets a space-separated list of default CSS style class(es) to be applied
     * when this cell is rendered. This value will be passed through as the
     * "class" attribute on generated markup.
     * 
     * @param cellStyleClass
     *            list of default CSS style classes
     */
    void setDefaultCellStyleClass(String cellStyleClass);

    /**
     * Returns a space-separated list of CSS style class(es) to be applied when
     * this cell is rendered. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @return list of CSS style classes
     */
    String getCellStyleClass();

    /**
     * Sets a space-separated list of CSS style class(es) to be applied when
     * this cell is rendered. This value will be passed through as the "class"
     * attribute on generated markup.
     * 
     * @param cellStyleClass
     *            list of CSS style classes
     */
    void setCellStyleClass(String cellStyleClass);
}
