/*
 * $Id: LexicalUnitImpl2.java,v 1.1.2.1 2012/11/15 10:13:48 oeuillot Exp $
 */
package org.rcfaces.css.internal.ext;

import org.w3c.css.sac.LexicalUnit;

import com.steadystate.css.parser.LexicalUnitImpl;

public class LexicalUnitImpl2 extends LexicalUnitImpl {

    private static final long serialVersionUID = -2314126727258297216L;

    protected LexicalUnitImpl2(LexicalUnit prev, short sacOperatorEqual) {
        super(prev, sacOperatorEqual);
    }

    public static LexicalUnit createEqual(final LexicalUnit prev) {
        return new LexicalUnitImpl2(prev, LexicalUnit2.SAC_OPERATOR_EQUAL);
    }

    @Override
    public String toString() {
        switch (getLexicalUnitType()) {
        case LexicalUnit2.SAC_OPERATOR_EQUAL:
            return "=";

        case SAC_URI:
            return "url(\"" + getStringValue() + "\")";
        }

        return super.toString();
    }

    @Override
    public String toDebugString() {
        if (getLexicalUnitType() == LexicalUnit2.SAC_OPERATOR_EQUAL) {
            return "SAC_OPERATOR_EQUAL";
        }
        return super.toDebugString();
    }

}
