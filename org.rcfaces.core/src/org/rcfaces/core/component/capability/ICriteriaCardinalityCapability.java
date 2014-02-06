/*
 * $Id: ICriteriaCardinalityCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * An int value specifying the type of multiple criteria authorized:
 * <ul>
 * <li> 1:optional|?: none or one criterion </li>
 * <li> 2:zeromany|*: any number of criteria or none </li>
 * <li> 3:one|1: one and only one criterion </li>
 * <li> 4:onemany|+: one or more criteria </li>
 * </ul>
 * 
 * cf. ICardinality for constant values
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface ICriteriaCardinalityCapability extends ICardinality {

    /**
     * Default cardinality for selection feature.
     */
    int DEFAULT_CARDINALITY = ICardinality.OPTIONAL_CARDINALITY;

    /**
     * Returns an int value specifying the type of multiple criteria
     * authorized. cf. ICardinality for constant values
     * 
     * @return 1: none or one criterion|2: any number of criteria or none|3:
     *         one and only one criterion|4: one or more criteria
     */
    int getCriteriaCardinality();

    /**
     * Sets an int value specifying the type of multiple criteria authorized.
     * cf. ICardinality for constant values
     * 
     * @param criteriaCardinality
     *            1: none or one criterion|2: any number of criteria or
     *            none|3: one and only one criterion|4: one or more criteria
     */
    void setCriteriaCardinality(int criteriaCardinality);
}
