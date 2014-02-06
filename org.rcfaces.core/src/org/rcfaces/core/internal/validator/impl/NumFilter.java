/*
 * $Id: NumFilter.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.validator.IClientValidatorContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class NumFilter extends AbstractDynamicPatternTask {
    

    private static final Log LOG = LogFactory.getLog(NumFilter.class);

    protected String getRegularExpression(IClientValidatorContext context) {
        StringAppender sa = new StringAppender("[0-9");

        if (getBoolParameter(context, "num.signed", false)) {
            String sup = getParameter(context, "num.negSign", "-");
            if (sup != null && sup.length() > 0) {
                sa.append(buildEscaped(sup));
            }
        }

        int showDecimal = 0;
        String numDecimal = getParameter(context, "num.decimal");
        if (numDecimal != null) {
            if ("true".equalsIgnoreCase(numDecimal)) {
                showDecimal = -1;
            } else {
                showDecimal = Integer.parseInt(numDecimal);
            }
        }

        if (showDecimal != 0) {
            String sup = getParameter(context, "num.decSign");
            if (sup != null && sup.length() > 0) {
                sa.append(buildEscaped(sup));
            }
        }

        String sup = getParameter(context, "num.sepSign");
        if (sup != null && sup.length() > 0) {
            sa.append(buildEscaped(sup));
        }

        sa.append(']');

        return sa.toString();
    }
}
