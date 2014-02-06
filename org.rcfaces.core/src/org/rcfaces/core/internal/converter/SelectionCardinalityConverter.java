/*
 * $Id: SelectionCardinalityConverter.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.ISelectionCardinalityCapability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class SelectionCardinalityConverter extends CardinalityConverter {
    

    public static final Converter SINGLETON = new SelectionCardinalityConverter();

    private static final Integer DEFAULT_CARDINALITY = new Integer(
            ISelectionCardinalityCapability.DEFAULT_CARDINALITY);

    protected Object getDefaultCardinality() {
        return DEFAULT_CARDINALITY;
    }

}
