/*
 * $Id: AbstractDynamicPatternTask.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.IFilterTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public abstract class AbstractDynamicPatternTask extends
        AbstractClientValidatorTask implements IFilterTask {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractDynamicPatternTask.class);

    private static final String PATTERN_ATTRIBUTE = "regexp.pattern";

    protected abstract String getRegularExpression(
            IClientValidatorContext context);

    protected Pattern getPattern(IClientValidatorContext context) {

        Pattern pattern = (Pattern) context.getAttribute(PATTERN_ATTRIBUTE);
        if (pattern != null) {
            return pattern;
        }

        String regex = getRegularExpression(context);

        pattern = getPattern(regex);

        context.setAttribute(PATTERN_ATTRIBUTE, pattern);

        return pattern;
    }

    public boolean applyFilter(IClientValidatorContext context, char keyChar) {

        Pattern pattern = getPattern(context);

        return pattern.matcher(new StringAppender(keyChar)).matches();
    }
}
