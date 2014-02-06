/*
 * $Id: ParamUtils.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.iterator.IParameterIterator;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.tools.ParameterTools;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class ParamUtils {
    private static final Log LOG = LogFactory.getLog(ParamUtils.class);

    public static String formatMessage(UIComponent component, String pattern) {
        if (pattern == null || pattern.length() < 1) {
            return pattern;
        }

        IParameterIterator it = ParameterTools.listParameters(component);
        if (it.hasNext() == false) {
            return pattern;
        }

        UIParameter parameters[] = it.toArray();

        StringAppender[] segments = new StringAppender[2];
        for (int i = 0; i < segments.length; ++i) {
            segments[i] = new StringAppender();
        }
        int part = 0;
        // int formatNumber = 0;
        boolean inQuote = false;
        int braceStack = 0;

        char chs[] = pattern.toCharArray();

        next_char: for (int i = 0; i < chs.length; ++i) {
            char ch = chs[i];
            if (part == 0) {
                if (ch == '\'') {
                    if (i + 1 < chs.length && chs[i + 1] == '\'') {
                        segments[part].append(ch); // handle doubles
                        i++;
                        continue;
                    }

                    inQuote = !inQuote;
                    continue;
                }

                if (ch == '{' && !inQuote) {
                    part = 1;
                    continue;
                }

                segments[part].append(ch);
                continue;
            }

            if (inQuote) { // just copy quotes in parts
                segments[part].append(ch);
                if (ch == '\'') {
                    inQuote = false;
                }
                continue;
            }

            switch (ch) {

            case ',':
                if (part < 3) {
                    part++;
                    continue next_char;
                }

                segments[part].append(ch);
                continue next_char;

            case '{':
                braceStack++; // Accolade dans une accolade !
                segments[part].append(ch);
                continue next_char;

            case '}':
                if (braceStack == 1) {
                    part = 0;
                    makeFormat(segments, parameters);
                    // formatNumber++;

                    segments[1].setLength(0);
                    segments[2].setLength(0);
                    segments[3].setLength(0);
                    continue next_char;
                }

                braceStack--;
                segments[part].append(ch);
                continue next_char;

            case '\'':
                inQuote = true;
                continue next_char;

            default:
                segments[part].append(ch);
                break;
            }
        }

        if (braceStack == 0 && part > 0) {
            throw new IllegalArgumentException(
                    "Unmatched braces in the pattern.");
        }

        return segments[0].toString();
    }

    private static void makeFormat(StringAppender[] segments,
            UIParameter parameters[]) {

        String key = segments[1].toString();

        if (key.length() < 1) {
            return;
        }

        if (Character.isDigit(key.charAt(0))) {
            int idx = Integer.parseInt(key);
            if (idx < 0 || idx >= parameters.length) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Index out of bounds  0=<" + idx + "<"
                            + parameters.length);
                }
                return;
            }

            Object v = parameters[idx].getValue();
            if (v == null) {
                return;
            }

            segments[0].append(String.valueOf(v));

            return;
        }

        for (int i = 0; i < parameters.length; i++) {
            UIParameter parameter = parameters[i];

            if (key.equals(parameter.getName()) == false) {
                continue;
            }

            Object v = parameter.getValue();
            if (v == null) {
                return;
            }

            segments[0].append(String.valueOf(v));
            return;
        }
    }
}
