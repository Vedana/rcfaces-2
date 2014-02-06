/*
 * $Id: ISelectionCardinalityCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * An int value specifying the type of multiple selection authorized:
 * <ul>
 * <li> 1:optional|?: none or one selection </li>
 * <li> 2:zeromany|*: any number of selections or none </li>
 * <li> 3:one|1: one and only one selection </li>
 * <li> 4:onemany|+: one or more selection </li>
 * </ul>
 * 
 * cf. ICardinality for constant values
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ISelectionCardinalityCapability extends ICardinality {

    /**
     * Default cardinality for selection feature.
     */
    int DEFAULT_CARDINALITY = ICardinality.OPTIONAL_CARDINALITY;

    /**
     * Returns an int value specifying the type of multiple selection
     * authorized. cf. ICardinality for constant values
     * 
     * @return 1: none or one selection|2: any number of selections or none|3:
     *         one and only one selection|4: one or more selection
     */
    int getSelectionCardinality();

    /**
     * Sets an int value specifying the type of multiple selection authorized.
     * cf. ICardinality for constant values
     * 
     * @param selectionCardinality
     *            1: none or one selection|2: any number of selections or
     *            none|3: one and only one selection|4: one or more selection
     */
    void setSelectionCardinality(int selectionCardinality);
}
