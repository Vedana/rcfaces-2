/*
 * $Id: PaddingFormatter.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IMaxTextLengthCapability;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.IFormatterTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class PaddingFormatter extends AbstractClientValidatorTask implements
        IFormatterTask {
    

    private static final Log LOG = LogFactory.getLog(PaddingFormatter.class);

    public String applyFormatter(IClientValidatorContext context, String value) {

        if (value == null || value.length() == 0) {
            return value;
        }

        UIComponent component = context.getComponentRenderContext()
                .getComponent();
        if ((component instanceof IMaxTextLengthCapability) == false) {
            return value;
        }

        int maxTextLength = ((IMaxTextLengthCapability) component)
                .getMaxTextLength();
        if (maxTextLength < 1) {
            return value;
        }

        String padderString = getParameter(context, "padder.value", " ");
        boolean leftSide = getBoolParameter(context, "padder.leftSide", true);
        boolean modifyInput = getBoolParameter(context, "padder.modifyInput",
                true);

        int length = maxTextLength - value.length();
        if (length <= 0) {
            return value;
        }

        StringAppender sa = new StringAppender(maxTextLength);

        if (leftSide == false) {
            sa.append(value);
        }

        sa.append(padderString.charAt(0), length);

        if (leftSide) {
            sa.append(value);
        }

        if (modifyInput) {
            context.setInputValue(value);
        }

        return sa.toString();
    }
}
