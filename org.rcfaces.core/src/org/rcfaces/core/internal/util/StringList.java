/*
 * $Id: StringList.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 */
package org.rcfaces.core.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class StringList {

    private static final Log LOG = LogFactory.getLog(StringList.class);

    private static final char STRING_DEFAULT_SEPARATOR = ',';

    private static final char STRING_DEFAULT_ESCAPE = '\\';

    private static final String STRING_DEFAULT_NULL = "##null##";

    public static String[] parseTokensList(String value) {

        if (value == null) {
            return null;
        }
        if (value.length() < 1) {
            return new String[] { /* value */};
        }

        char chs[] = value.toCharArray();

        List<String> l = null;

        StringAppender sa = new StringAppender(32);

        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c == STRING_DEFAULT_ESCAPE) {
                i++;
                if (i == chs.length) {
                    break;
                }
                sa.append(chs[i]);
                continue;
            }

            if (c == STRING_DEFAULT_SEPARATOR) {
                if (l == null) {
                    l = new ArrayList<String>(value.length() / 8 + 1);
                }

                if (STRING_DEFAULT_NULL.equals(sa.toString().trim())) {
                    l.add(null);
                    sa.setLength(0);
                    continue;
                }

                l.add(sa.toString().trim());

                sa.setLength(0);
                continue;
            }

            sa.append(chs[i]);
        }

        if (STRING_DEFAULT_NULL.equals(sa.toString().trim())) {
            if (l == null) {
                return new String[] { null };
            }
            l.add(null);
            return l.toArray(new String[l.size()]);
        }

        if (l == null) {
            return new String[] { sa.toString().trim() };
        }

        l.add(sa.toString());

        return l.toArray(new String[l.size()]);
    }

    public static int countTokens(String value) {

        char chs[] = value.toCharArray();

        int cnt = 1;
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c == STRING_DEFAULT_ESCAPE) {
                i++; // On ignore le suivant
                continue;
            }

            if (c == STRING_DEFAULT_SEPARATOR) {
                cnt++;
                continue;
            }
        }

        return cnt;
    }

    public static String getFirstToken(String value) {

        if (value == null || value.length() < 1) {
            return value;
        }

        StringAppender sa = new StringAppender(32);

        char chs[] = value.toCharArray();

        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c == STRING_DEFAULT_ESCAPE) {
                i++;
                if (i == chs.length) {
                    break;
                }
                sa.append(chs[i]);
                continue;
            }

            if (c == STRING_DEFAULT_SEPARATOR) {
                break;
            }

            sa.append(chs[i]);
        }

        return sa.toString().trim();
    }

    public static String joinTokens(Collection<String> collection) {
        StringAppender sa = new StringAppender(collection.size() * 16);

        for (String token : collection) {
            if (token == null) {
                token = STRING_DEFAULT_NULL;
            }

            if (sa.length() > 0) {
                sa.append(STRING_DEFAULT_SEPARATOR);
            }

            char chs[] = token.trim().toCharArray();
            for (int i = 0; i < chs.length; i++) {
                char c = chs[i];

                if (c == STRING_DEFAULT_SEPARATOR || c == STRING_DEFAULT_ESCAPE
                        || c == '=') {
                    sa.append(STRING_DEFAULT_ESCAPE);
                }

                sa.append(c);
            }
        }

        return sa.toString();
    }

    public static Map<String, String> parseTokensMap(String value) {

        if (value == null) {
            return null;
        }
        if (value.length() < 1) {
            return Collections.emptyMap();
        }

        char chs[] = value.toCharArray();

        Map<String, String> map = new HashMap<String, String>();

        StringAppender sa = new StringAppender(32);
        String key = null;

        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c == '=') {
                key = sa.toString().trim();
                sa.setLength(0);
                continue;
            }

            if (c == STRING_DEFAULT_ESCAPE) {
                i++;
                if (i == chs.length) {
                    break;
                }

                c = chs[i];
                sa.append(chs[i]);
                continue;
            }

            if (c == STRING_DEFAULT_SEPARATOR) {
                if (key == null) {
                    map.put(sa.toString().trim(), "");

                } else {
                    map.put(key, sa.toString().trim());
                }

                key = null;
                sa.setLength(0);
                continue;
            }

            sa.append(chs[i]);
        }

        if (key == null) {
            map.put(sa.toString().trim(), "");

        } else {
            map.put(key, sa.toString().trim());
        }

        return map;
    }

    public static String joinTokens(Map<String, String> collection) {
        StringAppender sa = new StringAppender(collection.size() * 32);

        for (Map.Entry<String, String> entry : collection.entrySet()) {
            String token = entry.getKey();
            String value = entry.getValue();

            if (sa.length() > 0) {
                sa.append(STRING_DEFAULT_SEPARATOR);
            }

            char chs[] = token.trim().toCharArray();
            for (int i = 0; i < chs.length; i++) {
                char c = chs[i];

                if (c == STRING_DEFAULT_SEPARATOR || c == STRING_DEFAULT_ESCAPE
                        || c == '=') {
                    sa.append(STRING_DEFAULT_ESCAPE);
                }

                sa.append(c);
            }

            if (value != null && value.length() > 0) {
                sa.append('=');

                chs = token.trim().toCharArray();
                for (int i = 0; i < chs.length; i++) {
                    char c = chs[i];

                    if (c == STRING_DEFAULT_SEPARATOR
                            || c == STRING_DEFAULT_ESCAPE || c == '=') {
                        sa.append(STRING_DEFAULT_ESCAPE);
                    }

                    sa.append(c);
                }
            }
        }

        return sa.toString();
    }
}
