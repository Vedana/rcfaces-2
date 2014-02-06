/*
 * $Id: LiteralTwoDigitYearConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import javax.faces.convert.Converter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class LiteralTwoDigitYearConverter extends TwoDigitYearConverter {
    

    public static final Converter SINGLETON = new LiteralTwoDigitYearConverter();

    protected boolean isLiteral() {
        return true;
    }
}
