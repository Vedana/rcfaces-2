/*
 * $Id: INumberFormatTypeCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A string value specifying the data type :
 * <ul>
 * <li> number </li>
 * <li> integer </li>
 * <li> currency </li>
 * <li> percent </li>
 * </ul>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface INumberFormatTypeCapability {
    /**
     * 
     */
    int NUMBER_FORMAT_TYPE = 0;

    String NUMBER_FORMAT_TYPE_NAME = "number";

    /**
     * 
     */
    int INTEGER_FORMAT_TYPE = 1;

    String INTEGER_FORMAT_TYPE_NAME = "integer";

    /**
     * 
     */
    int CURRENCY_FORMAT_TYPE = 2;

    String CURRENCY_FORMAT_TYPE_NAME = "currency";

    /**
     * 
     */
    int PERCENT_FORMAT_TYPE = 3;

    String PERCENT_FORMAT_TYPE_NAME = "percent";

    /**
     * Returns a int value specifying the data type.
     * 
     * @return 0:number|1:integer|2:currency|3:percent
     */
    int getNumberFormatType();

    /**
     * Sets a int value specifying the data type.
     * 
     * @param numberFormatType
     *            0:number|1:integer|2:currency|3:percent
     */
    void setNumberFormatType(int numberFormatType);

}
