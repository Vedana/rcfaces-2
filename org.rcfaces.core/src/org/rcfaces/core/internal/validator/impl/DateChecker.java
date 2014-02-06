/*
 * $Id: DateChecker.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.validator.ICheckerTask;
import org.rcfaces.core.validator.IClientValidatorContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class DateChecker extends AbstractClientValidatorTask implements
        ICheckerTask {
    

    private static final Log LOG = LogFactory.getLog(DateChecker.class);

    private static final Pattern DATE_SEPARATOR_PATTERN = Pattern
            .compile("^[0-9]*$");

    public String applyChecker(IClientValidatorContext context, String value) {
        if (value == null || value.length() == 0) {
            return value;
        }

        String sep = getParameter(context, "date.sepSign");
        String set = "[" + buildEscaped(sep) + "]";
        char sepChar = sep.charAt(0);

        // String sTmp = value;
        String d = null;
        String m = null;
        String y = null;
        boolean invalidFormat = true;

        Matcher matcher = DATE_SEPARATOR_PATTERN.matcher(value);

        // Check if digits only
        if (matcher.matches()) {
            switch (matcher.end()) {
            case 8:
            case 6:
                y = value.substring(4);
            case 4:
                m = value.substring(2, 4);
            case 2:
                d = value.substring(0, 2);
                invalidFormat = false;
                break;
            case 1:
                d = value;
            case 0:
                invalidFormat = false;
                break;
            }
            // Otherwise we have separators
        } else {
            Pattern pattern = getPattern("^(\\d{1,2})?" + set + "(\\d{1,2})?"
                    + set + "?(\\d{2}|\\d{4})?$");

            matcher = pattern.matcher(value);

            if (matcher.matches()) {
                invalidFormat = false;

                d = matcher.group(0);
                m = matcher.group(1);
                y = matcher.group(2);
            }
        }

        // Check valid string
        if (invalidFormat) {
            String formattedValue = value.replaceAll("(" + set + ")", String
                    .valueOf(sepChar));

            context.setInputValue(formattedValue);
            context.setOutputValue(formattedValue);
            return null;
        }

        int pivot = getIntParameter(context, "date.pivot", 0);

        IProcessContext processContext = context.getComponentRenderContext()
                .getRenderContext().getProcessContext();

        int iy;
        int im;
        int id;

        Calendar calendar = processContext.getUserCalendar();
        synchronized (calendar) {
            calendar.setTime(new Date());

            if (y != null && y.length() > 0) {
                iy = Integer.parseInt(y);

                if (iy < 100) {
                    iy += (iy > pivot) ? 1900 : 2000;
                }
            } else {
                iy = calendar.get(Calendar.YEAR);
            }

            if (m != null && m.length() > 0) {
                im = Integer.parseInt(m);

            } else {
                im = calendar.get(Calendar.MONTH) + 1;
            }

            if (d != null && d.length() > 0) {
                id = Integer.parseInt(d);

            } else {
                id = calendar.get(Calendar.DAY_OF_MONTH);
            }
        }

        // Build input and output value
        String formattedValue = ((id < 10) ? "0" : "") + id + sepChar
                + ((im < 10) ? "0" : "") + im + sepChar + iy;

        context.setInputValue(formattedValue);
        context.setOutputValue(formattedValue);

        int min = getIntParameter(context, "date.min", 1850);
        int max = getIntParameter(context, "date.max", 2100);

        // Check valid year boundaries
        if (iy < min || iy > max) {
            return null;
        }

        synchronized (calendar) {
            calendar.setTime(new Date());

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            calendar.set(Calendar.YEAR, iy);
            calendar.set(Calendar.MONTH, im - 1);
            calendar.set(Calendar.DAY_OF_MONTH, id);

            if (calendar.get(Calendar.YEAR) != iy
                    || calendar.get(Calendar.MONTH) != im - 1
                    || calendar.get(Calendar.DAY_OF_MONTH) != id) {
                return null;
            }
        }

        return formattedValue;
    }
}
