/*
 * $Id: AdditionalInformationCardinalityConverter.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IAdditionalInformationCardinalityCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */
public class AdditionalInformationCardinalityConverter extends
        CardinalityConverter {
    
    public static final Converter SINGLETON = new AdditionalInformationCardinalityConverter();

    private static final Integer DEFAULT_CARDINALITY = new Integer(
            IAdditionalInformationCardinalityCapability.DEFAULT_CARDINALITY);

    @Override
    protected Object getDefaultCardinality() {
        return DEFAULT_CARDINALITY;
    }

}
