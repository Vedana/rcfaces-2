/*
 * $Id: OutlinedLabelTools.java,v 1.1 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;

import org.rcfaces.core.component.capability.IOutlinedLabelCapability;
import org.rcfaces.core.component.capability.IOutlinedLabelCapability.Method;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:22 $
 */
public class OutlinedLabelTools {

    private static final EnumSet<IOutlinedLabelCapability.Method> EMPTY_ENUM = EnumSet
            .noneOf(IOutlinedLabelCapability.Method.class);

    public static EnumSet<IOutlinedLabelCapability.Method> normalize(
            String method) {

        if (method == null || method.trim().length() == 0) {
            return EMPTY_ENUM;
        }

        StringTokenizer st = new StringTokenizer(method, ",; ");

        Set<IOutlinedLabelCapability.Method> ms = new HashSet<IOutlinedLabelCapability.Method>(
                8);

        for (; st.hasMoreTokens();) {
            String token = st.nextToken().toLowerCase();

            if ("ignorecase".equals(token) || "ignore-case".equals(token)) {
                ms.add(Method.IgnoreCase);
                continue;
            }
            if ("ignoreaccents".equals(token) || "ignore-accents".equals(token)) {
                ms.add(Method.IgnoreAccents);
               continue;
            }
            if ("multiple".equals(token)) {
                ms.add(Method.Multiple);
                ms.remove(Method.Server);
                continue;
            }
            if ("startswith".equals(token)) {
                ms.add(Method.StartsWith);
                ms.remove(Method.FullText);
                ms.remove(Method.WordOnly);
                ms.remove(Method.Server);
                continue;
            }
            if ("word".equals(token) || "eachword".equals(token)
                    || "each-word".equals(token)) {
                ms.remove(Method.StartsWith);
                ms.remove(Method.FullText);
                ms.add(Method.WordOnly);
                ms.remove(Method.Server);
                continue;
            }
            if ("fulltext".equals(token)) {
                ms.remove(Method.StartsWith);
                ms.add(Method.FullText);
                ms.remove(Method.WordOnly);
                ms.remove(Method.Server);
                continue;
            }
            if ("server".equals(token)) {
                ms.clear();
                ms.add(Method.Server);
                continue;
            }

            throw new FacesException("Unknown outlined label method '" + token
                    + "'");
        }

        if (ms.isEmpty()) {
            return null;
        }

        EnumSet<IOutlinedLabelCapability.Method> ms2 = EnumSet.copyOf(ms);

        return ms2;
    }

    public static String format(EnumSet<IOutlinedLabelCapability.Method> ms) {

        StringAppender sa = new StringAppender(ms.size() * 3);

        for (IOutlinedLabelCapability.Method m : ms) {
            if (sa.length() > 0) {
                sa.append(',');
            }
            switch (m) {
            case FullText:
                sa.append("ft");
                break;
            case IgnoreAccents:
                sa.append("ia");
                break;
            case IgnoreCase:
                sa.append("ic");
                break;
            case Multiple:
                sa.append("mt");
                break;
            case StartsWith:
                sa.append("sw");
                break;
            case WordOnly:
                sa.append("ew");
                break;
            case Server:
                sa.append("se");
                break;
            }
        }

        return sa.toString();

    }
}
