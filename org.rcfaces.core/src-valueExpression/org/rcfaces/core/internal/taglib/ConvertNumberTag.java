/*
 * $Id: ConvertNumberTag.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.taglib;

import java.util.Locale;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.NumberConverter;
import javax.servlet.jsp.JspException;

import org.rcfaces.core.converter.AbstractNumberConverter;
import org.rcfaces.core.internal.converter.LocaleConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class ConvertNumberTag extends CameliaConverterTag {
    

    private static final long serialVersionUID = 9211078836220394550L;

    private static final String DEFAULT_NUMBER_TYPE = "number";

    private ValueExpression currencyCode;

    private ValueExpression currencySymbol;

    private ValueExpression groupingUsed;

    private ValueExpression integerOnly;

    private ValueExpression maxFractionDigits;

    private ValueExpression maxIntegerDigits;

    private ValueExpression minFractionDigits;

    private ValueExpression minIntegerDigits;

    private ValueExpression locale;

    private ValueExpression pattern;

    private ValueExpression type;

    private ValueExpression defaultValue;

    public ConvertNumberTag() {
        initializeFields();
    }

    public void release() {
        super.release();
        initializeFields();
    }

    private void initializeFields() {
        currencyCode = null;
        currencySymbol = null;
        groupingUsed = null;
        integerOnly = null;
        maxFractionDigits = null;
        maxIntegerDigits = null;
        minFractionDigits = null;
        locale = null;
        pattern = null;
        type = null;
    }

    public void setCurrencyCode(ValueExpression currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrencySymbol(ValueExpression currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setGroupingUsed(ValueExpression groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    public void setIntegerOnly(ValueExpression integerOnly) {
        this.integerOnly = integerOnly;
    }

    public void setMaxFractionDigits(ValueExpression maxFractionDigits) {
        this.maxFractionDigits = maxFractionDigits;
    }

    public void setMaxIntegerDigits(ValueExpression maxIntegerDigits) {
        this.maxIntegerDigits = maxIntegerDigits;
    }

    public void setMinFractionDigits(ValueExpression minFractionDigits) {
        this.minFractionDigits = minFractionDigits;
    }

    public void setMinIntegerDigits(ValueExpression minIntegerDigits) {
        this.minIntegerDigits = minIntegerDigits;
    }

    public void setLocale(ValueExpression locale) {
        this.locale = locale;
    }

    public void setPattern(ValueExpression pattern) {
        this.pattern = pattern;
    }

    public void setType(ValueExpression type) {
        this.type = type;
    }

    public final void setDefaultValue(ValueExpression defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int doStartTag() throws JspException {
        setConverterId(getDefaultConverterId());

        return super.doStartTag();
    }

    protected String getDefaultConverterId() {
        return "org.rcfaces.Number";
    }

    protected Converter createConverter() throws JspException {

        NumberConverter result = (NumberConverter) super.createConverter();

        FacesContext facesContext = FacesContext.getCurrentInstance();

        ELContext elContext = facesContext.getELContext();

        if (currencyCode != null) {
            String c = null;

            if (currencyCode.isLiteralText()) {
                c = currencyCode.getExpressionString();

            } else {
                c = (String) currencyCode.getValue(elContext);
            }

            result.setCurrencyCode(c);
        }

        if (currencySymbol != null) {

            if (currencySymbol.isLiteralText()) {
                result.setCurrencySymbol(currencySymbol.getExpressionString());

            } else {
                result.setCurrencySymbol((String) currencySymbol
                        .getValue(elContext));
            }
        }

        if (pattern != null) {
            if (pattern.isLiteralText()) {
                result.setPattern(pattern.getExpressionString());

            } else {
                result.setPattern((String) pattern.getValue(elContext));
            }
        }

        String t = null;
        if (type != null) {
            if (type.isLiteralText()) {
                t = type.getExpressionString();

            } else {
                t = (String) type.getValue(elContext);
            }
        }

        if (t == null) {
            t = DEFAULT_NUMBER_TYPE;
        }

        result.setType(t);

        if (groupingUsed != null) {
            boolean gu;

            if (groupingUsed.isLiteralText()) {
                gu = Boolean.valueOf(type.getExpressionString()).booleanValue();

            } else {
                gu = ((Boolean) type.getValue(elContext)).booleanValue();
            }

            result.setGroupingUsed(gu);

        }

        if (integerOnly != null) {
            boolean io;

            if (integerOnly.isLiteralText()) {
                io = Boolean.valueOf(integerOnly.getExpressionString())
                        .booleanValue();

            } else {
                io = ((Boolean) integerOnly.getValue(elContext)).booleanValue();
            }

            result.setIntegerOnly(io);
        }

        if (maxFractionDigits != null) {
            int mfd;

            if (groupingUsed.isLiteralText()) {
                mfd = Integer.parseInt(integerOnly.getExpressionString());

            } else {
                mfd = ((Integer) integerOnly.getValue(elContext)).intValue();
            }

            result.setMaxFractionDigits(mfd);
        }

        if (maxIntegerDigits != null) {
            int mid;

            if (maxIntegerDigits.isLiteralText()) {
                mid = Integer.parseInt(maxIntegerDigits.getExpressionString());

            } else {
                mid = ((Integer) maxIntegerDigits.getValue(elContext))
                        .intValue();
            }

            result.setMaxIntegerDigits(mid);
        }

        if (minFractionDigits != null) {
            int mfd;

            if (minFractionDigits.isLiteralText()) {
                mfd = Integer.parseInt(minFractionDigits.getExpressionString());

            } else {
                mfd = ((Integer) minFractionDigits.getValue(elContext))
                        .intValue();
            }

            result.setMinFractionDigits(mfd);
        }

        if (minIntegerDigits != null) {
            int mid;

            if (minIntegerDigits.isLiteralText()) {
                mid = Integer.parseInt(minIntegerDigits.getExpressionString());

            } else {
                mid = ((Integer) minIntegerDigits.getValue(elContext))
                        .intValue();
            }

            result.setMinIntegerDigits(mid);
        }

        if (locale != null) {
            Locale loc;

            if (locale.isLiteralText()) {
                loc = (Locale) LocaleConverter.SINGLETON.getAsObject(
                        facesContext, null, locale.getExpressionString());

            } else {
                loc = (Locale) locale.getValue(elContext);
            }

            result.setLocale(loc);
        }

        if (result instanceof AbstractNumberConverter) {
            if (defaultValue != null) {
                Object defValue;

                if (locale.isLiteralText()) {
                    defValue = defaultValue.getExpressionString();

                } else {
                    defValue = defaultValue.getValue(elContext);
                }

                ((AbstractNumberConverter) result).setDefaultValue(String
                        .valueOf(defValue));
            }
        }

        return result;
    }
}