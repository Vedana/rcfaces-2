/*
 * $Id: GroupNumberLiteral.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.NumberLiteral;

public class GroupNumberLiteral extends AbstractGroupLiterals {

    public boolean process(JsStats stats, IJsFile jsFile) {
        return process(stats, jsFile, new ISetLiteralCondition() {

            public boolean testLiteral(Assignment assignment,
                    Assignment original) {
                if (NumberLiteral.class.isInstance(assignment.getRight()) == false) {
                    return false;
                }

                if (original == null) {
                    return true;
                }

                if (((NumberLiteral) assignment.getRight()).getNumber() == ((NumberLiteral) original
                        .getRight()).getNumber()) {
                    return true;
                }

                return false;
            }

            public String getLiteralName(Assignment assignment) {
                return String.valueOf(((NumberLiteral) assignment.getRight())
                        .getNumber());
            }

            public String getName() {
                return "Number";
            }

        });
    }

}
