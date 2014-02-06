/*
 * $Id: CheckCardinalityConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.ICheckCardinalityCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class CheckCardinalityConverter extends CardinalityConverter {
    

    public static final Converter SINGLETON = new CheckCardinalityConverter();

    private static final Integer DEFAULT_CARDINALITY = new Integer(
            ICheckCardinalityCapability.DEFAULT_CARDINALITY);

    protected Object getDefaultCardinality() {
        return DEFAULT_CARDINALITY;
    }

}
