/*
 * $Id: IAdditionalInformationCardinalityCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * An int value indicating the cardinality (number of additional-informations
 * allowed) for this component.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IAdditionalInformationCardinalityCapability extends
        ICardinality {

    /**
     * Default cardinality for additional information feature.
     */
    int DEFAULT_CARDINALITY = ICardinality.ZEROMANY_CARDINALITY;

    /**
     * Returns an int value indicating the cardinality (number of
     * additional-informations allowed) for this component.
     * 
     * @return 1:?,optional|2:*,zeroMany|3:1,one+|4:+,oneMany
     */
    int getAdditionalInformationCardinality();

    /**
     * Sets an int value indicating the cardinality (number of
     * additional-informations allowed) for this component.
     * 
     * @param additionalInformationCardinality
     *            1:?,optional|2:*,zeroMany|3:1,one+|4:+,oneMany
     *            default=zeroMany
     */
    void setAdditionalInformationCardinality(int additionalInformationCardinality);
}
