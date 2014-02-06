/*
 * $Id: LowerCaseTranslator.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.ITranslatorTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public class LowerCaseTranslator extends AbstractPatternTask implements
        ITranslatorTask {
    

    private static final Log LOG = LogFactory.getLog(LowerCaseTranslator.class);

    private static final Pattern LOWERCASE_PATTERN = Pattern
            .compile("[��������������������������]");

    protected Pattern getPattern(IClientValidatorContext context) {
        return LOWERCASE_PATTERN;
    }

    public char applyTranslator(IClientValidatorContext context, char keyChar) {
        int keyCode = keyChar;

        if (keyCode >= 65 && keyCode <= 90) {
            return (char) (keyCode + 32);
        }
        if (keyCode > 127 && applyFilter(context, keyChar)) {
            return (char) (keyCode + 32);
        }
        return keyChar;
    }

}
