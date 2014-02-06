/*
 * $Id: Length.java,v 1.1 2013/01/11 15:45:05 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.layout;

import javax.faces.FacesException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:05 $
 */
public class Length {
    public static final int UNIT_UNKNOWN = 0;

    public static final int PIXEL_UNIT = 1;

    public static final int PERCENT_UNIT = 2;

    public static final int ZERO_UNIT = 4;

    public static final Length ZERO = new Length(0, ZERO_UNIT);

    private final double number;

    private final int unit;

    public Length(double number, int unit) {
        this.number = number;
        this.unit = unit;
    }

    public Length applyParentLength(double l) {
        if (unit == ZERO_UNIT) {
            return this;
        }

        if (unit == PERCENT_UNIT) {
            return new Length(number * l / 100.0, PIXEL_UNIT);
        }

        throw new FacesException("Can not apply length");
    }

    public double getNumber() {
        return number;
    }

    public int getUnit() {
        return unit;
    }

    public static Length getLength(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim().toLowerCase();
        if (value.length() == 0) {
            return null;
        }

        char chs[] = value.toCharArray();

        int i = 0;
        // Recherche le debut !
        for (; i < chs.length; i++) {
            char c = chs[i];
            if (Character.isDigit(c) || c == '-' || c == '.') {
                break;
            }
        }
        if (i == chs.length) {
            return null; // Rien !
        }

        boolean negative = false;
        char c = chs[i];
        if (c == '-') {
            i++;
            negative = true;
        }

        double v = 0;
        int decimal = -1;
        // Recherche la fin
        for (; i < chs.length; i++) {
            c = chs[i];

            if (c == '.') {
                if (decimal >= 0) {
                    throw new FacesException("Bad margin unit '" + value + "'.");
                }
                decimal = 0;
                continue;
            }

            if (Character.isDigit(c) == false) {
                break;
            }

            if (decimal >= 0) {
                decimal++;
            }

            v = v * 10 + (c - '0');
        }

        if (decimal > 0) {
            v /= Math.pow(10, decimal);
        }

        // Passe des espaces ....
        for (; i < chs.length; i++) {
            c = chs[i];
            if (Character.isWhitespace(c) == false) {
                break;
            }
        }

        if (v == 0.0) {
            if (i < chs.length) {
                c = chs[i];

                if (Character.isLetter(c) == false && c != '%') {
                    throw new FacesException("Bad value '" + value + "'.");
                }
            }

            return ZERO;
        }

        if (negative) {
            v = -v;
        }

        // La fin !
        int unit = UNIT_UNKNOWN;
        if (i < chs.length) {
            String unitName = value.substring(i).trim();
            if ("%".equals(unitName)) {
                unit = PERCENT_UNIT;

            } else if ("px".equals(unitName)) {
                unit = PIXEL_UNIT;

            } else {
                throw new FacesException("Unknown unit '" + unit
                        + "' for value '" + value + "'");
            }
        }

        return new Length(v, unit);
    }
}