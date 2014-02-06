/*
 * $Id: LiteralNumberConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 */
package org.rcfaces.core.internal.converter;

import javax.faces.convert.Converter;

import org.rcfaces.core.converter.AbstractNumberConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class LiteralNumberConverter extends AbstractNumberConverter {
    

    public static final Converter SINGLETON = new LiteralNumberConverter();

}
