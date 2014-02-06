/*
 * $Id: HourChecker.java,v 1.2 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.validator.ICheckerTask;
import org.rcfaces.core.validator.IClientValidatorContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:06 $
 */
public class HourChecker extends AbstractClientValidatorTask implements
        ICheckerTask {
    

    private static final Log LOG = LogFactory.getLog(HourChecker.class);

    private static final Pattern HOUR_SEPARATOR_PATTERN = Pattern
            .compile("^\\d*$");

    public String applyChecker(IClientValidatorContext context, String value) {
        if (value == null || value.length() == 0) {
            return value;
        }

        String sep = getParameter(context, "hour.sepSign");
        String set = "[" + buildEscaped(sep) + "]";
        char sepChar = sep.charAt(0);

        // String sTmp = value;
        String h = null;
        String m = null;
        String sec = null;
        boolean invalidFormat = true;

        Matcher matcher = HOUR_SEPARATOR_PATTERN.matcher(value);

        // Check if digits only
        if (matcher.matches()) {
            switch (matcher.end()) {
            case 6:
                sec = value.substring(4, 2);
            case 4:
                m = value.substring(2, 2);
            case 2:
                h = value.substring(0, 2);
                invalidFormat = false;
                break;
            case 1:
                h = value;
            case 0:
                invalidFormat = false;
                break;
            }
            // Otherwise we have separators
        } else {
            Pattern pattern = getPattern("^(\\d{1,2})?" + set + "(\\d{1,2})?"
                    + set + "?(\\d{1,2})?$");

            matcher = pattern.matcher(value);

            if (matcher.matches()) {
                invalidFormat = false;

                h = matcher.group(0);
                m = matcher.group(1);
                sec = matcher.group(2);
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

        // IProcessContext processContext =
        // context.getComponentRenderContext().getRenderContext().getProcessContext();

        // Compute hour
        int ih;
        try {
            ih = (h != null) ? Integer.parseInt(h) : 0;

        } catch (NumberFormatException ex) {
            LOG.debug("Invalid hours '" + value + "'", ex);
            ih = -1;
        }

        if (ih < 0 || ih > 23) {
            context.setLastError("VALIDATION HEURE", "Heure invalide",
                    FacesMessage.SEVERITY_ERROR);
            return null;
        }

        int im;
        try {
            im = (m != null) ? Integer.parseInt(m) : 0;

        } catch (NumberFormatException ex) {
            LOG.debug("Invalid minutes '" + value + "'", ex);
            im = -1;
        }

        if (im < 0 || im > 23) {
            context.setLastError("VALIDATION HEURE", "Minutes invalides",
                    FacesMessage.SEVERITY_ERROR);
            return null;
        }

        int is;
        try {
            is = (sec != null) ? Integer.parseInt(sec) : 0;

        } catch (NumberFormatException ex) {
            LOG.debug("Invalid seconds '" + value + "'", ex);
            is = -1;
        }

        if (is < 0 || is > 23) {
            context.setLastError("VALIDATION HEURE", "Secondes invalides",
                    FacesMessage.SEVERITY_ERROR);
            return null;
        }

        // Build input and output value
        String formattedValue = ((ih < 10) ? "0" : "") + ih + sepChar
                + ((im < 10) ? "0" : "") + im + sepChar
                + ((is < 10) ? "0" : "") + is;

        context.setInputValue(formattedValue);
        context.setOutputValue(formattedValue);

        return formattedValue;
    }
}
