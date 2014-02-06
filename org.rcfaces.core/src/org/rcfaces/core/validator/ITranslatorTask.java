/*
 * $Id: ITranslatorTask.java,v 1.1 2011/04/12 09:25:46 oeuillot Exp $
 */
package org.rcfaces.core.validator;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:46 $
 */
public interface ITranslatorTask extends IClientValidatorTask {

    char applyTranslator(IClientValidatorContext context, char keyChar);
}
