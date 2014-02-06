/*
 * $Id: ICheckCardinalityCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * An int value indicating the cardinality (number of check allowed) for this
 * componenent.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ICheckCardinalityCapability extends ICardinality {

    /**
     * Default cardinality for check feature.
     */
    int DEFAULT_CARDINALITY = ICardinality.ZEROMANY_CARDINALITY;

    /**
     * Returns an int value indicating the cardinality (number of check allowed)
     * for this componenent.
     * 
     * @return 1:?,optional|2:*,zeroMany|3:1,one+|4:+,oneMany
     */
    int getCheckCardinality();

    /**
     * Sets an int value indicating the cardinality (number of check allowed)
     * for this componenent.
     * 
     * @param checkCardinality
     *            1:?,optional|2:*,zeroMany|3:1,one+|4:+,oneMany
     *            default=zeroMany
     */
    void setCheckCardinality(int checkCardinality);
}
