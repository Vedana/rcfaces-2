/*
 * $Id: AlphaFilter.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
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
public class AlphaFilter extends AbstractDynamicPatternTask {
    

    private static final Log LOG = LogFactory.getLog(AlphaFilter.class);

    protected String getRegularExpression(IClientValidatorContext context) {

        StringAppender exp = new StringAppender("[", 16);

        appendRegExp(exp);

        String sup = getParameter(context, "alpha.otherChars");
        if (sup != null && sup.length() > 0) {
            exp.append(buildEscaped(sup));
        }

        exp.append(']');

        return exp.toString();
    }

    protected void appendRegExp(StringAppender sa) {
        sa.append("a-zA-Z");
    }

}
