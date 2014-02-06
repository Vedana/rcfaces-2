/*
 * $Id: TypeTokenizer.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public final class TypeTokenizer {

    private final char chs[];

    private int offset = -1;

    private int end = 0;

    private final String separator;

    public TypeTokenizer(String s) {
        this(s, " \r\t\n");
    }

    public TypeTokenizer(String s, String separator) {
        this.separator = separator;
        chs = s.toCharArray();
    }

    public boolean hasMoreTokens() {
        if (offset >= 0) {
            return true;
        }
        if (end == -2) {
            return false;
        }

        for (; end < chs.length;) {
            char c = chs[end];
            if (separator.indexOf(c) >= 0) {
                end++;
                continue;
            }

            break;
        }

        offset = end;

        if (offset == chs.length) {
            end = -2;
            return false;
        }

        int stack = 0;
        for (; end < chs.length;) {
            char c = chs[end];
            if (c == '<') {
                stack++;
                end++;
                continue;
            }
            if (c == '>') {
                stack--;
                end++;
                continue;
            }

            if (stack == 0 && separator.indexOf(c) >= 0) {
                break;
            }

            end++;
        }

        if (end == offset) {
            end = -2;
            return false;
        }

        return true;
    }

    public String nextToken() {
        if (hasMoreTokens() == false) {
            throw new IllegalStateException();
        }

        String s = new String(chs, offset, end - offset);

        offset = -1;

        return s;
    }

    public String endOfText() {
        if (hasMoreTokens() == false) {
            return "";
        }

        return new String(chs, offset, chs.length - offset);
    }
}