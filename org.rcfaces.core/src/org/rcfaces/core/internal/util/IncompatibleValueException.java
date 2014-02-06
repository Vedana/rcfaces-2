/*
 * $Id: IncompatibleValueException.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import javax.faces.FacesException;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class IncompatibleValueException extends FacesException {
    

    private static final long serialVersionUID = -2959146007402643205L;

    public IncompatibleValueException(Object value, String types) {
        super("Value '" + value.getClass() + "' is not compatible with: "
                + types, null);
    }

}
