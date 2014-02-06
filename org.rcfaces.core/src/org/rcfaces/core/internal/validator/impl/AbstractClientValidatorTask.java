/*
 * $Id: AbstractClientValidatorTask.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.IClientValidatorTask;
import org.rcfaces.core.validator.IParameter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class AbstractClientValidatorTask implements IClientValidatorTask {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractClientValidatorTask.class);

    private static final String PATTERN_ATTRIBUTE = "regexp.pattern";

    private static final Pattern ESCAPE_REGEXP = Pattern.compile(
            "([\\\\\\/\\.\\*\\+\\?\\|\\(\\)\\[\\]\\{\\}\\-\\^])",
            Pattern.DOTALL);

    private static final Map regExpressions = new HashMap(64);

    protected String getParameter(IClientValidatorContext context,
            String parameterName) {
        IParameter parameters[] = context.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(parameterName)) {
                return parameters[i].getValue();
            }
        }

        return null;
    }

    protected String getParameter(IClientValidatorContext context,
            String parameterName, String defaultValue) {
        String s = getParameter(context, parameterName);
        if (s != null) {
            return s;
        }

        return defaultValue;
    }

    protected int getIntParameter(IClientValidatorContext context,
            String parameterName, int defaultValue) {
        String s = getParameter(context, parameterName);
        if (s == null) {
            return defaultValue;
        }

        return Integer.parseInt(s);
    }

    protected boolean getBoolParameter(IClientValidatorContext context,
            String parameterName, boolean defaultValue) {
        String s = getParameter(context, parameterName);
        if (s == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(s);
    }

    protected String buildEscaped(String regEx) {
        if (regEx == null || regEx.length() < 1) {
            return "";
        }

        return ESCAPE_REGEXP.matcher(regEx).replaceAll("\\\\$1");
    }

    protected Pattern getPattern(String regEx) {
        Pattern pattern;

        synchronized (regExpressions) {
            pattern = (Pattern) regExpressions.get(regEx);
            if (pattern == null) {
                pattern = Pattern.compile(regEx);
            }
        }

        return pattern;

    }
}
