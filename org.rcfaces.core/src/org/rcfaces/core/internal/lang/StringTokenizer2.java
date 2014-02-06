/*
 * $Id: StringTokenizer2.java,v 1.1 2011/04/12 09:25:21 oeuillot Exp $
 */
package org.rcfaces.core.internal.lang;

import java.util.NoSuchElementException;

/**
 * 
 * @author Java Team + Olivier Oeuillot (latest modification by $Author:
 *         oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:21 $
 */
public class StringTokenizer2 {

    private static final int MAX_STRING_SIZE = 16;

    private final char buffer[];

    private final String original;

    private final int maxPosition;

    private final boolean returnDelimiters;

    private String delimiters;

    private int currentPosition;

    private int newPosition;

    private boolean delimsChanged;

    private char maxDelimChar;

    public StringTokenizer2(String str, String delimiters, boolean returnDelims) {
        this.buffer = str.toCharArray();
        this.maxPosition = this.buffer.length;
        this.original = (maxPosition <= MAX_STRING_SIZE) ? str : null;
        this.returnDelimiters = returnDelims;

        setDelimiter(delimiters);

        currentPosition = 0;
        newPosition = -1;
        delimsChanged = false;

    }

    public StringTokenizer2(String str, String delim) {
        this(str, delim, false);
    }

    public StringTokenizer2(String str) {
        this(str, " \t\n\r\f");
    }

    private void setDelimiter(String delimiters) {
        if (delimiters == null) {
            throw new NullPointerException();
        }
        this.delimiters = delimiters;

        char m = 0;
        for (int i = 0; i < delimiters.length(); i++) {
            char c = delimiters.charAt(i);
            if (m < c) {
                m = c;
            }
        }
        this.maxDelimChar = m;
    }

    private int skipDelimiters(int position) {
        if (returnDelimiters) {
            return position;
        }

        for (; position < maxPosition; position++) {
            char c = buffer[position];
            if ((c > maxDelimChar) || (delimiters.indexOf(c) < 0)) {
                return position;
            }
        }
        return position;
    }

    /**
     * Skips ahead from startPos and returns the index of the next delimiter
     * character encountered, or maxPosition if no such delimiter is found.
     */
    private int scanToken(int startPos) {
        int position = startPos;
        for (; position < maxPosition; position++) {
            char c = buffer[position];
            if ((c <= maxDelimChar) && (delimiters.indexOf(c) >= 0)) {
                break;
            }
        }

        if (returnDelimiters && (startPos == position)) {
            // On a rien trouvé, on cherche un séparateur
            char c = buffer[position];
            if ((c <= maxDelimChar) && (delimiters.indexOf(c) >= 0)) {
                position++;
            }
        }
        return position;
    }

    public boolean hasMoreTokens() {
        newPosition = skipDelimiters(currentPosition);

        return (newPosition < maxPosition);
    }

    public String nextToken() {

        currentPosition = (newPosition >= 0 && !delimsChanged) ? newPosition
                : skipDelimiters(currentPosition);

        /* Reset these anyway */
        delimsChanged = false;
        newPosition = -1;

        if (currentPosition >= maxPosition) {
            throw new NoSuchElementException();
        }

        int start = currentPosition;
        currentPosition = scanToken(currentPosition);

        if (original != null) {
            return original.substring(start, currentPosition);
        }

        return new String(buffer, start, currentPosition - start);
    }

    public String nextToken(String delim) {
        setDelimiter(delim);

        /* delimiter string specified, so set the appropriate flag. */
        delimsChanged = true;

        return nextToken();
    }

    public int countTokens() {
        int count = 0;

        for (int pos = currentPosition; pos < maxPosition; count++) {
            pos = skipDelimiters(pos);
            if (pos >= maxPosition) {
                // Fin de la string
                return maxPosition;
            }

            pos = scanToken(pos);
        }

        return count;
    }
}
