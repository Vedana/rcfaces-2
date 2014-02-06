/*
 * $Id: NumChecker.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.validator.ICheckerTask;
import org.rcfaces.core.validator.IClientValidatorContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class NumChecker extends AbstractClientValidatorTask implements
        ICheckerTask {
    

    private static final Log LOG = LogFactory.getLog(NumChecker.class);

    private static final Pattern ZERO_SEPARATOR_PATTERN = Pattern
            .compile("^(0+)([0-9]*)$");

    public String applyChecker(IClientValidatorContext context, String value) {
        if (value == null || value.length() == 0) {
            return value;
        }

        String sep = getParameter(context, "num.sepSign");
        if (sep != null && sep.length() > 0) {
            Pattern pattern = getPattern("[" + buildEscaped(sep) + "]");

            value = pattern.matcher(value).replaceAll("");
        }

        String neg = getParameter(context, "num.negSign", "-");
        if (value.lastIndexOf(neg) > 0) {
            value = neg + value.replaceAll(buildEscaped(neg), "");
        }

        String dec = getParameter(context, "num.decSign");
        Pattern pattern = getPattern("^(" + buildEscaped(neg) + "?)(\\d*)(["
                + buildEscaped(dec) + "]?)(\\d*)$");

        // Check expression
        Matcher matcher = pattern.matcher(value);

        // No match
        if (matcher.matches() == false) {
            return null;
        }

        int showDecimal = 0;
        String numDecimalParameter = getParameter(context, "num.decimal");
        if (numDecimalParameter != null) {
            if ("true".equalsIgnoreCase(numDecimalParameter)) {
                showDecimal = -1;
            } else {
                showDecimal = Integer.parseInt(numDecimalParameter);
            }
        }

        // Get parts
        String ip = "0";
        String d = "";
        String dp = (showDecimal > 0) ? "0" : "";

        int groupCount = matcher.groupCount();
        String n = matcher.group(1);
        if (groupCount > 1) {
            ip = matcher.group(2);
            if (groupCount > 2) {
                d = dec.substring(0, 1);
                if (groupCount > 3) {
                    dp = matcher.group(4);
                }
            }
        }

        int decimal = getIntParameter(context, "num.cutDecimal", -1);

        if (decimal > 0 && dp.length() > decimal) {
            dp = dp.substring(0, decimal);

        } else if (decimal == 0) {
            d = "";
            dp = "";
        }

        if (ip.length() > 1) {
            // Retire les 0 au debut !

            matcher = ZERO_SEPARATOR_PATTERN.matcher(ip);

            if (matcher.matches()) {
                ip = (matcher.groupCount() > 1) ? matcher.group(1) : "0";
            }
        }

        int lenDp = showDecimal;
        if (lenDp < 1) {
            lenDp = 1;
        }

        if (dp.length() > lenDp) {
            char chs[] = dp.toCharArray();
            int last = chs.length;
            for (int i = chs.length; i > lenDp; i--) {
                if (chs[i - 1] != '0') {
                    break;
                }

                last = i - 1;
            }

            if (last != chs.length) {
                dp = new String(chs, 0, last);
            }
        }

        if (d.length() > 0 && dp.length() == 0) {
            if (showDecimal > 0) {
                dp = "0";
            } else {
                d = "";
            }
        }

        // Rebuild string
        value = n + ip + d + dp;

        return value;
    }
}
