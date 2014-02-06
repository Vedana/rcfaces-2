/*
 * $Id: LiteralDateConverter.java,v 1.2 2013/07/03 12:25:04 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import javax.faces.convert.Converter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:04 $
 */
public class LiteralDateConverter extends DateConverter {
    

    public static final Converter SINGLETON = new LiteralDateConverter();

    protected boolean isLiteral() {
        return true;
    }
}
