/*
 * $Id: IClientValidator.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component;

import javax.faces.validator.Validator;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface IClientValidator extends Validator {
    String getExpression();
}
