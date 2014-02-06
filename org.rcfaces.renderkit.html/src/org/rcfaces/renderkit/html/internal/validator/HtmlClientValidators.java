/*
 * $Id: HtmlClientValidators.java,v 1.1 2013/11/13 12:53:33 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.validator;

import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.LengthValidator;
import javax.faces.validator.LongRangeValidator;
import javax.faces.validator.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:33 $
 */
public class HtmlClientValidators {

    private static final Log LOG = LogFactory
            .getLog(HtmlClientValidators.class);

    private static final HtmlClientValidators singleton = new HtmlClientValidators();

    public String convertFromValidatorToExpression(
            IHtmlRenderContext renderContext, Validator validator) {

        if (validator instanceof LongRangeValidator) {
            return getLongRangeValidatorExpression(renderContext,
                    (LongRangeValidator) validator);
        }

        if (validator instanceof DoubleRangeValidator) {
            return getDoubleRangeValidatorExpression(renderContext,
                    (DoubleRangeValidator) validator);
        }

        if (validator instanceof LengthValidator) {
            return getLengthValidatorExpression(renderContext,
                    (LengthValidator) validator);
        }

        return null;
    }

    private String getLengthValidatorExpression(
            IHtmlRenderContext renderContext, LengthValidator validator) {
        int min = validator.getMinimum();
        int max = validator.getMaximum();

        if (min < 1 && max < 1) {
            return null;
        }

        StringAppender sa = new StringAppender(64);

        sa.append("this,");

        if (min > 0 || max > 0) {
            if (max < 1) {
                max = 0;
            }
            sa.append(max);
        }
        if (min > 0) {
            sa.append(',').append(min);
        }

        return convertValidatorExpression(renderContext, "f_vb",
                "LengthValidator", sa.toString());
    }

    private String getDoubleRangeValidatorExpression(
            IHtmlRenderContext renderContext, DoubleRangeValidator validator) {

        double min = validator.getMinimum();
        double max = validator.getMaximum();

        if (min == Double.MIN_VALUE && max == Double.MAX_VALUE) {
            return null;
        }

        StringAppender sa = new StringAppender(64);

        sa.append("this,");

        if (min != Double.MIN_VALUE || max != Double.MAX_VALUE) {
            if (min == Double.MAX_VALUE) {
                sa.append("!0");
            } else {
                sa.append(min);
            }
        }
        if (max != Double.MAX_VALUE) {
            sa.append(',').append(max);
        }

        return convertValidatorExpression(renderContext, "f_vb",
                "DoubleRangeValidator", sa.toString());
    }

    private String getLongRangeValidatorExpression(
            IHtmlRenderContext renderContext, LongRangeValidator validator) {
        long min = validator.getMinimum();
        long max = validator.getMaximum();

        if (min == Long.MIN_VALUE && max == Long.MAX_VALUE) {
            return null;
        }

        StringAppender sa = new StringAppender(64);

        sa.append("this,");

        if (min != Double.MIN_VALUE || max != Double.MAX_VALUE) {
            if (min == Double.MAX_VALUE) {
                sa.append("!0");
            } else {
                sa.append(min);
            }
        }
        if (max != Double.MAX_VALUE) {
            sa.append(',').append(max);
        }

        return convertValidatorExpression(renderContext, "f_vb",
                "LongRangeValidator", sa.toString());
    }

    private String convertValidatorExpression(IHtmlRenderContext renderContext,
            String clazz, String method, String params) {

        IJavaScriptRenderContext scriptRenderContext = renderContext
                .getJavaScriptRenderContext();

        StringAppender sa = new StringAppender(clazz.length() + 1
                + method.length() + params.length());

        if (clazz != null) {
            if (scriptRenderContext != null) {
                clazz = scriptRenderContext.convertSymbol(null, clazz);
            }

            sa.append(clazz);
            sa.append('.');
        }

        if (scriptRenderContext != null) {
            method = scriptRenderContext.convertSymbol(clazz, method);
        }
        sa.append(method);

        sa.append('(');
        sa.append(params);
        sa.append(')');

        return sa.toString();
    }

    public static HtmlClientValidators get() {
        return singleton;
    }
}
